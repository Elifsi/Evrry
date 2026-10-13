-- ===========================================================================
-- 20261012000001_grocery_supermarket_extensions.sql
-- Grocery Supermarket Partner Extensions
-- Platform: Evrry Super App
-- Elifsi Technologies Private Limited
--
-- Adds barcode, SKU, stock quantity, and unit to catalog items,
-- pick-and-pack item verification, weight adjustments, and
-- out-of-stock customer substitution RPCs.
-- ===========================================================================

-- 1. Catalog item extensions for supermarket retail operations
ALTER TABLE public.catalog_items
  ADD COLUMN IF NOT EXISTS barcode TEXT,
  ADD COLUMN IF NOT EXISTS sku TEXT,
  ADD COLUMN IF NOT EXISTS stock_quantity INTEGER NOT NULL DEFAULT 10 CHECK (stock_quantity >= 0),
  ADD COLUMN IF NOT EXISTS unit TEXT NOT NULL DEFAULT 'pack';

CREATE INDEX IF NOT EXISTS catalog_items_barcode_idx ON public.catalog_items (barcode) WHERE barcode IS NOT NULL;
CREATE INDEX IF NOT EXISTS catalog_items_sku_idx ON public.catalog_items (sku) WHERE sku IS NOT NULL;

-- 2. Orders table extension for substitution tracking
ALTER TABLE public.orders
  ADD COLUMN IF NOT EXISTS substitution_proposals JSONB NOT NULL DEFAULT '[]'::jsonb;

-- 3. Order items extension for pick-and-pack checklist and measured weight
ALTER TABLE public.order_items
  ADD COLUMN IF NOT EXISTS measured_weight NUMERIC(6,3),
  ADD COLUMN IF NOT EXISTS is_packed BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS substitution_status TEXT NOT NULL DEFAULT 'none' CHECK (substitution_status IN ('none', 'proposed', 'approved', 'rejected')),
  ADD COLUMN IF NOT EXISTS substituted_item_id UUID REFERENCES public.catalog_items(id);

-- 4. Stored Procedure: Propose Grocery Item Substitution
CREATE OR REPLACE FUNCTION public.propose_grocery_substitution(
  p_order_id UUID,
  p_order_item_id UUID,
  p_substitute_item_id UUID,
  p_notes TEXT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
  v_orig_item RECORD;
  v_sub_item RECORD;
  v_proposal JSONB;
BEGIN
  -- Get original order item
  SELECT id, order_id, name, unit_price_paisa, quantity
  INTO v_orig_item
  FROM public.order_items
  WHERE id = p_order_item_id AND order_id = p_order_id;

  IF NOT FOUND THEN
    RAISE EXCEPTION 'Order item not found in specified order';
  END IF;

  -- Get substitute catalog item
  SELECT id, name, price_paisa, barcode, sku, unit
  INTO v_sub_item
  FROM public.catalog_items
  WHERE id = p_substitute_item_id;

  IF NOT FOUND THEN
    RAISE EXCEPTION 'Substitute item not found in catalog';
  END IF;

  -- Update order item substitution status
  UPDATE public.order_items
  SET substitution_status = 'proposed',
      substituted_item_id = p_substitute_item_id
  WHERE id = p_order_item_id;

  -- Build proposal object
  v_proposal := jsonb_build_object(
    'order_item_id', v_orig_item.id,
    'original_item_name', v_orig_item.name,
    'original_price_paisa', v_orig_item.unit_price_paisa,
    'substitute_item_id', v_sub_item.id,
    'substitute_item_name', v_sub_item.name,
    'substitute_price_paisa', v_sub_item.price_paisa,
    'substitute_unit', v_sub_item.unit,
    'notes', p_notes,
    'status', 'proposed',
    'proposed_at', now()
  );

  -- Append proposal to order
  UPDATE public.orders
  SET substitution_proposals = substitution_proposals || v_proposal,
      updated_at = now()
  WHERE id = p_order_id;

  RETURN v_proposal;
END;
$$;

-- 5. Stored Procedure: Resolve Grocery Substitution (Consumer Approval/Rejection)
CREATE OR REPLACE FUNCTION public.resolve_grocery_substitution(
  p_order_id UUID,
  p_order_item_id UUID,
  p_approved BOOLEAN
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
  v_sub_item RECORD;
  v_new_subtotal BIGINT;
  v_new_total BIGINT;
  v_order RECORD;
BEGIN
  SELECT * INTO v_order FROM public.orders WHERE id = p_order_id;
  IF NOT FOUND THEN
    RAISE EXCEPTION 'Order not found';
  END IF;

  IF p_approved THEN
    -- Find substitute details
    SELECT ci.* INTO v_sub_item
    FROM public.order_items oi
    JOIN public.catalog_items ci ON ci.id = oi.substituted_item_id
    WHERE oi.id = p_order_item_id;

    IF FOUND THEN
      -- Replace item name and price
      UPDATE public.order_items
      SET name = v_sub_item.name,
          unit_price_paisa = v_sub_item.price_paisa,
          substitution_status = 'approved',
          is_packed = TRUE
      WHERE id = p_order_item_id;
    END IF;
  ELSE
    -- Rejection marks as rejected
    UPDATE public.order_items
    SET substitution_status = 'rejected',
        is_packed = FALSE
    WHERE id = p_order_item_id;
  END IF;

  -- Recalculate totals
  SELECT coalesce(sum(line_total_paisa), 0) INTO v_new_subtotal
  FROM public.order_items
  WHERE order_id = p_order_id AND substitution_status <> 'rejected';

  v_new_total := v_new_subtotal + v_order.delivery_fee_paisa + v_order.platform_fee_paisa + v_order.tax_paisa - v_order.discount_paisa;

  UPDATE public.orders
  SET subtotal_paisa = v_new_subtotal,
      total_paisa = v_new_total,
      updated_at = now()
  WHERE id = p_order_id;

  RETURN jsonb_build_object(
    'order_id', p_order_id,
    'order_item_id', p_order_item_id,
    'status', CASE WHEN p_approved THEN 'approved' ELSE 'rejected' END,
    'new_subtotal_paisa', v_new_subtotal,
    'new_total_paisa', v_new_total
  );
END;
$$;

-- 6. Stored Procedure: Check off packed item & record loose weight
CREATE OR REPLACE FUNCTION public.pack_order_item(
  p_order_item_id UUID,
  p_is_packed BOOLEAN DEFAULT TRUE,
  p_measured_weight NUMERIC DEFAULT NULL
)
RETURNS VOID
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
  UPDATE public.order_items
  SET is_packed = p_is_packed,
      measured_weight = coalesce(p_measured_weight, measured_weight)
  WHERE id = p_order_item_id;
END;
$$;

-- 7. Stored Procedure: Complete Supermarket Pack Phase -> ready_for_pickup
CREATE OR REPLACE FUNCTION public.complete_order_packing(
  p_order_id UUID
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
  v_unpacked_count INTEGER;
BEGIN
  -- Check if any non-rejected items are still unpacked
  SELECT count(*) INTO v_unpacked_count
  FROM public.order_items
  WHERE order_id = p_order_id
    AND is_packed = FALSE
    AND substitution_status NOT IN ('rejected');

  IF v_unpacked_count > 0 THEN
    RAISE EXCEPTION 'Cannot complete order packing: % items remain unpacked', v_unpacked_count;
  END IF;

  -- Transition status to ready_for_pickup
  UPDATE public.orders
  SET status = 'ready_for_pickup',
      updated_at = now()
  WHERE id = p_order_id;

  -- Append to order status history
  INSERT INTO public.order_status_history (order_id, status)
  VALUES (p_order_id, 'ready_for_pickup');

  RETURN jsonb_build_object(
    'order_id', p_order_id,
    'status', 'ready_for_pickup',
    'ready_at', now()
  );
END;
$$;

GRANT EXECUTE ON FUNCTION public.propose_grocery_substitution(UUID, UUID, UUID, TEXT) TO authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.resolve_grocery_substitution(UUID, UUID, BOOLEAN) TO authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.pack_order_item(UUID, BOOLEAN, NUMERIC) TO authenticated, service_role;
GRANT EXECUTE ON FUNCTION public.complete_order_packing(UUID) TO authenticated, service_role;

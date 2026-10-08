"use client";

import { useState, useEffect } from "react";
import {
  BookOpen,
  Plus,
  Search,
  CheckCircle2,
  XCircle,
  ToggleLeft,
  ToggleRight,
  Filter,
  DollarSign,
  Utensils,
  Sparkles,
} from "lucide-react";
import { PartnerHeader } from "@/components/PartnerHeader";
import {
  fetchMenuItems,
  toggleMenuItemAvailability,
} from "@/lib/partner/api";
import { PartnerMenuItem } from "@/lib/partner/types";

export default function MenuCatalogPage() {
  const [menuItems, setMenuItems] = useState<PartnerMenuItem[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>("all");
  const [searchQuery, setSearchQuery] = useState("");
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);
  const [actionMessage, setActionMessage] = useState<string | null>(null);

  // New Item State
  const [newName, setNewName] = useState("");
  const [newCategory, setNewCategory] = useState("Pizzas");
  const [newPrice, setNewPrice] = useState("750");
  const [newDescription, setNewDescription] = useState("");
  const [newIsVeg, setNewIsVeg] = useState(true);

  const loadMenu = async () => {
    setIsRefreshing(true);
    const data = await fetchMenuItems("str-01");
    setMenuItems(data);
    setIsRefreshing(false);
  };

  useEffect(() => {
    loadMenu();
  }, []);

  const handleToggle86 = async (itemId: string, currentStatus: boolean) => {
    const res = await toggleMenuItemAvailability(itemId, !currentStatus);
    if (res.success) {
      setMenuItems((prev) =>
        prev.map((item) =>
          item.id === itemId ? { ...item, isAvailable: res.isAvailable } : item
        )
      );
      setActionMessage(
        res.isAvailable
          ? "Item marked IN STOCK (Now live on customer app)"
          : "Item 86'd OUT OF STOCK (Hidden from customer app)"
      );
      setTimeout(() => setActionMessage(null), 4000);
    }
  };

  const handleAddItem = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newName.trim()) return;

    const newItem: PartnerMenuItem = {
      id: `menu-${Date.now()}`,
      storeId: "str-01",
      name: newName.trim(),
      category: newCategory,
      description: newDescription.trim() || "Chef's special preparation.",
      priceNpr: Number(newPrice) || 500,
      isVeg: newIsVeg,
      isAvailable: true,
      imageUrl: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=600&q=80",
    };

    setMenuItems((prev) => [newItem, ...prev]);
    setShowAddModal(false);
    setNewName("");
    setNewDescription("");
    setActionMessage(`Added "${newItem.name}" to menu catalog!`);
    setTimeout(() => setActionMessage(null), 4000);
  };

  const categories = ["all", ...Array.from(new Set(menuItems.map((m) => m.category)))];

  const filteredItems = menuItems.filter((item) => {
    if (selectedCategory !== "all" && item.category !== selectedCategory) return false;
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase();
      return (
        item.name.toLowerCase().includes(q) ||
        item.description.toLowerCase().includes(q) ||
        item.category.toLowerCase().includes(q)
      );
    }
    return true;
  });

  return (
    <div className="flex-1 flex flex-col h-screen overflow-y-auto bg-slate-50">
      <PartnerHeader
        title="Menu & Item 86's Catalog Builder"
        subtitle="Item Availability Toggles • Live Out-of-Stock Kill Switches • Pricing & Descriptions"
        onRefresh={loadMenu}
        isRefreshing={isRefreshing}
      />

      <div className="p-6 md:p-8 space-y-6 max-w-[1500px] w-full mx-auto">
        {/* Action Alert Banner */}
        {actionMessage && (
          <div className="bg-brand-600 text-white px-5 py-3 rounded-xl text-xs font-bold flex items-center justify-between shadow-xs animate-in fade-in">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 shrink-0" />
              <span>{actionMessage}</span>
            </div>
            <span className="font-mono text-[11px] opacity-90">Live Sync</span>
          </div>
        )}

        {/* Action Controls Bar */}
        <div className="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex-1 max-w-md relative">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search dishes, ingredients, or drinks..."
              className="w-full pl-9 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 transition-all"
            />
          </div>

          <div className="flex items-center gap-2.5 shrink-0">
            <button
              type="button"
              onClick={() => setShowAddModal(true)}
              className="px-4 py-2 bg-brand-600 hover:bg-brand-700 text-white text-xs font-bold rounded-xl transition-all flex items-center gap-1.5 shadow-xs"
            >
              <Plus className="w-4 h-4" />
              <span>Add New Dish / Item</span>
            </button>
          </div>
        </div>

        {/* Category Pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1">
          {categories.map((cat) => (
            <button
              key={cat}
              type="button"
              onClick={() => setSelectedCategory(cat)}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all capitalize ${
                selectedCategory === cat
                  ? "bg-brand-600 text-white shadow-xs"
                  : "bg-white border border-slate-200 text-slate-600 hover:bg-slate-50"
              }`}
            >
              {cat === "all" ? "All Categories" : cat}
            </button>
          ))}
        </div>

        {/* Items Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredItems.map((item) => (
            <div
              key={item.id}
              className={`bg-white rounded-2xl border shadow-xs overflow-hidden transition-all flex flex-col justify-between ${
                item.isAvailable
                  ? "border-slate-200/80 hover:border-slate-300"
                  : "border-rose-200 bg-rose-50/20 opacity-80"
              }`}
            >
              <div className="p-5 space-y-3">
                <div className="flex items-start justify-between gap-3">
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <span
                        className={`w-3 h-3 rounded-full border flex items-center justify-center shrink-0 ${
                          item.isVeg
                            ? "border-emerald-600 bg-white"
                            : "border-rose-600 bg-white"
                        }`}
                        title={item.isVeg ? "Vegetarian" : "Non-Vegetarian"}
                      >
                        <span
                          className={`w-1.5 h-1.5 rounded-full ${
                            item.isVeg ? "bg-emerald-600" : "bg-rose-600"
                          }`}
                        />
                      </span>
                      <h3 className="text-sm font-bold text-slate-900 leading-snug">
                        {item.name}
                      </h3>
                    </div>
                    <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400 block pl-5">
                      {item.category}
                    </span>
                  </div>

                  <span className="font-mono text-sm font-black text-slate-900 shrink-0">
                    NPR {item.priceNpr}
                  </span>
                </div>

                <p className="text-xs text-slate-500 line-clamp-2 pl-5">
                  {item.description}
                </p>
              </div>

              {/* Bottom 86 Out of Stock Bar */}
              <div
                className={`p-4 border-t flex items-center justify-between text-xs font-semibold ${
                  item.isAvailable
                    ? "bg-slate-50/50 border-slate-100"
                    : "bg-rose-50 border-rose-100 text-rose-800"
                }`}
              >
                <div className="flex items-center gap-2">
                  <span
                    className={`w-2 h-2 rounded-full ${
                      item.isAvailable ? "bg-emerald-500" : "bg-rose-500"
                    }`}
                  />
                  <span>
                    {item.isAvailable ? "Live & In Stock" : "86'd Out of Stock"}
                  </span>
                </div>

                <button
                  type="button"
                  onClick={() => handleToggle86(item.id, item.isAvailable)}
                  className={`px-3 py-1 rounded-lg text-xs font-bold transition-all border ${
                    item.isAvailable
                      ? "bg-white border-slate-200 text-slate-700 hover:bg-rose-50 hover:text-rose-700 hover:border-rose-200"
                      : "bg-emerald-600 border-emerald-600 text-white hover:bg-emerald-700"
                  }`}
                >
                  {item.isAvailable ? "Mark 86 (Out of Stock)" : "Reinstate to Menu"}
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Add New Item Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-slate-200 max-w-md w-full p-6 shadow-2xl space-y-4 animate-in fade-in zoom-in-95 duration-150">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-sm font-bold text-slate-900">
                Add New Dish to Menu
              </h3>
              <button
                type="button"
                onClick={() => setShowAddModal(false)}
                className="text-slate-400 hover:text-slate-600"
              >
                <XCircle className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleAddItem} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">
                  Dish / Product Name *
                </label>
                <input
                  type="text"
                  required
                  value={newName}
                  onChange={(e) => setNewName(e.target.value)}
                  placeholder="e.g. Chicken Choila with Beaten Rice"
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:border-brand-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">
                    Category
                  </label>
                  <select
                    value={newCategory}
                    onChange={(e) => setNewCategory(e.target.value)}
                    className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:border-brand-500"
                  >
                    <option value="Starters & Appetizers">Starters & Appetizers</option>
                    <option value="Pizzas">Pizzas</option>
                    <option value="Pastas & Mains">Pastas & Mains</option>
                    <option value="Desserts">Desserts</option>
                    <option value="Beverages">Beverages</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">
                    Price (NPR) *
                  </label>
                  <input
                    type="number"
                    required
                    value={newPrice}
                    onChange={(e) => setNewPrice(e.target.value)}
                    className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:border-brand-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">
                  Description
                </label>
                <textarea
                  rows={2}
                  value={newDescription}
                  onChange={(e) => setNewDescription(e.target.value)}
                  placeholder="Ingredients, preparation style, and allergen notes..."
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:border-brand-500"
                />
              </div>

              <div className="flex items-center gap-3">
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={newIsVeg}
                    onChange={(e) => setNewIsVeg(e.target.checked)}
                    className="w-4 h-4 text-brand-600 rounded"
                  />
                  <span className="text-xs font-medium text-slate-700">
                    Vegetarian (Green Dot)
                  </span>
                </label>
              </div>

              <div className="pt-2 flex items-center justify-end gap-2 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="px-4 py-2 border border-slate-200 text-slate-700 text-xs font-semibold rounded-xl hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-brand-600 hover:bg-brand-700 text-white text-xs font-bold rounded-xl shadow-xs"
                >
                  Save to Catalog
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

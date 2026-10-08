import { Laptop, UtensilsCrossed, ShoppingCart, Shirt, Hotel, Car, Wrench, Home, LucideIcon } from "lucide-react";
import { ServiceCategory } from "@/lib/types";

export const CATEGORY_ICON: Record<ServiceCategory, LucideIcon> = {
  food: UtensilsCrossed,
  grocery: ShoppingCart,
  rides: Car,
  hotels: Hotel,
  rentals: Home,
  electronics: Laptop,
  fashion: Shirt,
  services: Wrench,
};

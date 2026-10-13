import { test, expect } from "@playwright/test";

// Exercises the manual (non-AI) checkout path end to end: add an item from
// a restaurant menu, top up the wallet (it starts at ₹0 for a fresh
// session), pay from the wallet, and land on Activity with the order
// recorded. Deliberately not the AI-agent checkout path — that depends on
// a configured provider API key, which CI won't have; this path is fully
// deterministic and still covers the same cart -> priceCart -> wallet debit
// -> transaction pipeline the AI path shares.
test("adds an item to cart and completes checkout directly to activity", async ({ page }) => {
  await page.goto("/explore/food/rest-spicehouse");
  await expect(page.getByText("Spice House")).toBeVisible();

  await page.getByRole("button", { name: "ADD" }).first().click();

  await page.goto("/cart");
  await expect(page.getByRole("heading", { name: "Your cart" })).toBeVisible();

  await page.getByRole("button", { name: /^Place order/ }).click();

  await expect(page).toHaveURL(/\/activity/);
  await expect(page.getByText("North Indian Thali")).toBeVisible();
});

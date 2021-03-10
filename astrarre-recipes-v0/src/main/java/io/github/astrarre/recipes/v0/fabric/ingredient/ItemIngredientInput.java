package io.github.astrarre.recipes.v0.fabric.ingredient;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.recipes.v0.fabric.util.ItemIngredient;
import io.github.astrarre.recipes.v0.fabric.value.FabricValueParsers;

import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;

public class ItemIngredientInput implements RecipePart<ItemIngredient, Inventory> {
	@Override
	public ValueParser<ItemIngredient> parser() {
		return FabricValueParsers.ITEM_INGREDIENT;
	}

	@Override
	public boolean test(Inventory inp, ItemIngredient val) {
		return Inventories.remove(inp, stack -> val.matcher.matches(ItemKey.of(stack)), val.amount, true) == val.amount;
	}

	@Override
	public void apply(Inventory inp, ItemIngredient val) {
		Inventories.remove(inp, stack -> val.matcher.matches(ItemKey.of(stack)), val.amount, false);
	}
}

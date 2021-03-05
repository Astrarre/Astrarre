package io.github.astrarre.recipes.v0.fabric.ingredient;

import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.recipes.v0.fabric.value.FabricValueParsers;

import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class StrictItemStackInput implements RecipePart<ItemStack, Inventory> {
	@Override
	public ValueParser<ItemStack> parser() {
		return FabricValueParsers.ITEM_STACK;
	}

	@Override
	public boolean test(Inventory inp, ItemStack val) {
		val = val.copy();
		ItemStack finalVal = val;
		return Inventories.remove(inp, i -> ItemStack.areItemsEqual(finalVal, i) && ItemStack.areTagsEqual(finalVal, i), val.getCount(), true) == val.getCount();
	}

	@Override
	public void apply(Inventory inp, ItemStack val) {
		val = val.copy();
		ItemStack finalVal = val;
		Inventories.remove(inp, i -> ItemStack.areItemsEqual(finalVal, i) && ItemStack.areTagsEqual(finalVal, i), val.getCount(), false);
	}
}

package io.github.astrarre.recipes.v0.fabric.output;

import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.recipes.v0.fabric.value.FabricValueParsers;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class InventoryOutput implements RecipePart<ItemStack, Inventory> {
	@Override
	public ValueParser<ItemStack> parser() {
		return FabricValueParsers.ITEM_STACK;
	}

	@Override
	public boolean test(Inventory inp, ItemStack val) {
		return add(inp, val.copy(), true);
	}

	public static boolean add(Inventory inp, ItemStack val, boolean simulate) {
		for (int i = 0; i < inp.size(); i++) {
			if (inp.isValid(i, val)) {
				ItemStack stack = inp.getStack(i);
				if (stack.isEmpty() || (ItemStack.areTagsEqual(stack, val) && ItemStack.areItemsEqual(stack,
						val) && stack.getMaxCount() > stack.getCount())) {
					int missing = Math.min(stack.getMaxCount() - stack.getCount(), val.getCount());
					if (stack.isEmpty() && !simulate) {
						stack = val.copy();
						stack.setCount(missing);
						inp.setStack(i, stack);
					} else if (!simulate) {
						stack.increment(missing);
						inp.setStack(i, stack);
					}

					val.decrement(missing);
					if (val.isEmpty()) {
						break;
					}
				}
			}
		}
		return val.isEmpty();
	}

	@Override
	public void apply(Inventory inp, ItemStack val) {
		add(inp, val.copy(), false);
	}
}

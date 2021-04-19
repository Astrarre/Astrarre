package io.github.astrarre.transfer.v0.lba.adapters;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;

import net.minecraft.item.ItemStack;

public class ItemInsertableLimitedConsumer implements ItemInsertable {
	protected final LimitedConsumer<ItemStack> stack;

	public static ItemInsertable from(LimitedConsumer<ItemStack> stack) {
		if(stack instanceof ItemInsertable) {
			return (ItemInsertable) stack;
		} else {
			return new ItemInsertableLimitedConsumer(stack);
		}
	}

	protected ItemInsertableLimitedConsumer(LimitedConsumer<ItemStack> stack) {
		this.stack = stack;
	}

	@Override
	public ItemStack attemptInsertion(ItemStack stack, Simulation simulation) {
		if(this.stack.offer(stack, simulation)) {
			return stack;
		}
		return ItemStack.EMPTY;
	}
}

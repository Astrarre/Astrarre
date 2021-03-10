package io.github.astrarre.recipe_transfer.v0.api.outputs;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.recipes.v0.fabric.value.FabricValueParsers;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;

import net.minecraft.item.ItemStack;

public class ItemStackOutput implements RecipePart<ItemStack, Insertable<ItemKey>> {
	@Override
	public ValueParser<ItemStack> parser() {
		return FabricValueParsers.ITEM_STACK;
	}

	@Override
	public boolean test(Insertable<ItemKey> inp, ItemStack val) {
		try(Transaction transaction = new Transaction(false)) {
			return inp.insert(transaction, ItemKey.of(val), val.getCount()) == val.getCount();
		}
	}

	@Override
	public void apply(Insertable<ItemKey> inp, ItemStack val) {
		inp.insert(null, ItemKey.of(val), val.getCount());
	}
}

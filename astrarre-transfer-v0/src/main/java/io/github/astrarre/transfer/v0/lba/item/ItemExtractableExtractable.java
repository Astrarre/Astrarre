package io.github.astrarre.transfer.v0.lba.item;

public class ItemExtractableExtractable {} /*implements Extractable<ItemKey> {
	protected final ItemExtractable extractable;
	protected final ItemInsertableKey key;

	public ItemExtractableExtractable(ItemExtractable extractable) {
		this.extractable = extractable;
		this.key = new ItemInsertableKey();
	}

	@Override
	public void extract(@Nullable Transaction transaction, Insertable<ItemKey> insertable) {

	}

	@Override
	public int extract(@Nullable Transaction transaction, ItemKey type, int quantity) {
		ItemStack current = this.key.get(transaction);
		if(current.isEmpty() || type.isEqual(current)) {
			int combinedSize = Math.min(type.getMaxStackSize(), quantity + current.getCount());
			ItemStack success = type.createItemStack(combinedSize);
			ItemStack remainder = this.extractable.attemptExtraction(success, Simulation.SIMULATE);
			success.decrement(remainder.getCount());
			this.key.set(transaction, success);
			return success.getCount() - current.getCount();
		}
		return 0;
	}

	protected class ItemInsertableKey extends ObjectKeyImpl<ItemStack> {
		@Override
		protected ItemStack getRootValue() {
			return ItemStack.EMPTY;
		}

		@Override
		protected void setRootValue(ItemStack val) {
			ItemExtractableExtractable.this.extractable.extract(val, val.getCount());
		}
	}
}*/

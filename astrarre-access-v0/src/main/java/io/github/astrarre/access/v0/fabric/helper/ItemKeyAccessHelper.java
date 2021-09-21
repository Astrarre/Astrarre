package io.github.astrarre.access.v0.fabric.helper;

import io.github.astrarre.access.v0.api.MapFilter;
import io.github.astrarre.access.v0.api.helper.AbstractAccessHelper;
import io.github.astrarre.access.v0.api.helper.AccessHelpers;
import io.github.astrarre.itemview.v0.fabric.ItemKey;

public class ItemKeyAccessHelper<F> extends AbstractAccessHelper<ItemKey, F> {
	private final MapFilter<ItemKey, F> itemKeyTypes;
	private final ItemAccessHelper<F> items;
	public ItemKeyAccessHelper(AccessHelpers.Context<ItemKey, F> copyFrom) {
		super(copyFrom);
		this.items = new ItemAccessHelper<>(copyFrom.map(ItemKey::getItem));
		this.itemKeyTypes = new MapFilter<>(copyFrom.func(), copyFrom.empty());
	}

	public ItemAccessHelper<F> getItem() {
		return this.items;
	}

	/**
	 * registers a filter for an item that exactly matches the ItemKey
	 */
	public ItemKeyAccessHelper<F> forItemKey(ItemKey item, F function) {
		if (this.itemKeyTypes.add(item, function)) {
			this.andThen.accept(this.itemKeyTypes::get);
		}
		return this;
	}

	public ItemKeyAccessHelper<F> forItemKeys(F function, ItemKey... keys) {
		for (ItemKey key : keys) {
			this.forItemKey(key, function);
		}
		return this;
	}
}

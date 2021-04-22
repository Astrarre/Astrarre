package io.github.astrarre.transfer.v0.lba.item;

import java.util.Collections;
import java.util.Set;

import alexiil.mc.lib.attributes.item.filter.ExactItemFilter;
import alexiil.mc.lib.attributes.item.filter.ExactItemSetFilter;
import alexiil.mc.lib.attributes.item.filter.ExactItemStackFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemTagFilter;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.delegate.FilteringInsertable;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.util.v0.fabric.Tags;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;

public class ItemFilterFilteringInsertable extends FilteringInsertable<ItemKey> implements Provider {
	public final ItemFilter filter;
	public final Set<Item> items;
	public final Insertable<ItemKey> delegate;

	public ItemFilterFilteringInsertable(ItemFilter filter, Insertable<ItemKey> delegate) {
		super((object, quantity) -> filter.matches(object.createItemStack(quantity)), delegate);
		this.filter = filter;
		this.delegate = delegate;
		if(filter instanceof ExactItemFilter) {
			this.items = Collections.singleton(((ExactItemFilter) filter).item);
		} else if(filter instanceof ExactItemSetFilter) {
			this.items = ((ExactItemSetFilter) filter).getItems();
		} else if(filter instanceof ExactItemStackFilter) {
			this.items = Collections.singleton(((ExactItemStackFilter) filter).stack.getItem());
		} else if(filter instanceof ItemTagFilter) {
			this.items = Tags.get(((ItemTagFilter) filter).tag);
		} else {
			this.items = null;
		}
	}

	@Override
	public @Nullable Object get(Access<?> access) {
		if(access == FabricParticipants.ITEM_FILTERS) {
			return this.items;
		}
		return null;
	}
}

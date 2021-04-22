package io.github.astrarre.transfer.v0.api.filter;

import java.util.Collections;
import java.util.Set;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;

public class ItemFilteringInsertable extends FilteringInsertable<ItemKey> implements Provider {
	public final Set<Item> items;
	public ItemFilteringInsertable(Set<Item> items, Insertable<ItemKey> delegate) {
		super((object, quantity) -> items.contains(object.getItem()), delegate);
		this.items = Collections.unmodifiableSet(items);
	}

	@Override
	public @Nullable Object get(Access<?> access) {
		return access == FabricParticipants.ITEM_FILTERS ? this.items : null;
	}
}

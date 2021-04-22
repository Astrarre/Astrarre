package io.github.astrarre.transfer.v0.api.filter;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.util.v0.fabric.Tags;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public class ItemTagFilteringInsertable extends FilteringInsertable<ItemKey> implements Provider {
	public final Tag<Item> tag;
	public ItemTagFilteringInsertable(Tag<Item> tag, Insertable<ItemKey> delegate) {
		super((object, quantity) -> tag.contains(object.getItem()), delegate);
		this.tag = tag;
	}


	@Override
	public @Nullable Object get(Access<?> access) {
		if(access == FabricParticipants.ITEM_FILTERS) {
			return Tags.get(this.tag);
		}
		return null;
	}
}

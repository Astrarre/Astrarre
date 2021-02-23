package io.github.astrarre.transfer.internal.participantInventory;

import java.util.Set;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.func.Returns;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.itemview.v0.fabric.TaggedItem;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.generated.IntKeyImpl;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;

/**
 * searches a participant for a set of items
 */
public class SetMatchingInsertable implements Insertable<TaggedItem>, Provider {
	private final Set<Item> items;
	private final int toFind;
	public Key.Int found = new IntKeyImpl(0);

	public SetMatchingInsertable(Set<Item> items, int toFind) {
		this.items = items;
		this.toFind = toFind;
	}

	@Override
	public int insert(@Nullable Transaction transaction, TaggedItem type, int quantity) {
		if (this.items.contains(type.getItem())) {
			this.found.set(transaction, this.found.get(transaction) + quantity);
			return quantity;
		}
		return 0;
	}

	@Override
	public boolean isFull(@Nullable Transaction transaction) {
		return this.found.get(transaction) >= this.toFind;
	}

	@Override
	public <T> @Nullable T get(Access<? extends Returns<T>, T> access) {
		return access == FabricParticipants.FILTERS ? (T) this.items : null;
	}
}

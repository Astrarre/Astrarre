package io.github.astrarre.transfer.internal.inventory;

import java.util.Collections;
import java.util.Set;

import io.github.astrarre.itemview.v0.fabric.TaggedItem;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

/**
 * Limited compatibility, you can only see the first item in the participant. The reasoning is that accessing the Nth slot of a participant is O(N),
 * and for large inventories, it is simply not worth it.
 *
 * Essentially, the way this works is it acts like a buffer inventory, where there is one 'extraction' slot, and one 'insertion' slot.
 *
 * The extraction slot exposes a single item from the participant (perhaps we can randomize it, or increase the size in the future), emptying it
 * removes the item from the participant
 *
 * The insertion slot acts kindof like a void or buffer slot, where inserting an item shuttles it to the participant immediately and the slot is
 * re-opened. We rely on {@link Inventory#isValid(int, ItemStack)} to allow us to only accept items that the participant will actually take
 */
public class ParticipantInventory implements SidedInventory {
	private static final int BUFFER_EXTRACTION_SLOT = 0;
	private static final int BUFFER_INSERTION_SLOT = 1;

	public final Participant<TaggedItem> participant;
	private final InternalItemSlotParticipant slot = new InternalItemSlotParticipant();

	public ParticipantInventory(Participant<TaggedItem> participant) {this.participant = participant;}

	@Override
	public int size() {
		return 2;
	}

	@Override
	public boolean isEmpty() {
		return this.participant.isEmpty(null);
	}

	@Override
	public ItemStack getStack(int slot) {
		this.test(slot);
		if (slot == BUFFER_EXTRACTION_SLOT) {
			try (Transaction transaction = new Transaction(false)) {
				this.participant.extract(transaction, this.slot);
				return this.slot.getItemStack(transaction);
			}
		} else {
			// 'buffer insertion slot'
			return ItemStack.EMPTY;
		}
	}

	private void test(int slot) {
		if (slot != BUFFER_EXTRACTION_SLOT && slot != BUFFER_INSERTION_SLOT) {
			throw new IndexOutOfBoundsException(slot + " for size 2!");
		}
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		this.test(slot);
		if(slot == BUFFER_EXTRACTION_SLOT) {
			int max = this.slot.getMax(null);
			this.slot.setMax(amount);
			this.participant.extract(null, this.slot);
			ItemStack stack = this.slot.getItemStack(null);
			this.slot.clear(null);
			// reset max
			this.slot.setMax(max);
			return stack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.removeStack(slot, this.getMaxCountPerStack());
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.test(slot);
		// if slot = 0, then they are trying to insert into the one slot we said to not insert in.
		// but, we'll let it pass
		this.participant.insert(null, TaggedItem.of(stack), stack.getCount());
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {return true;}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		if (slot == 0) {
			return false;
		} else {
			try(Transaction transaction = new Transaction(false)) {
				int count = stack.getCount();
				return this.participant.insert(transaction, TaggedItem.of(stack), count) == count;
			}
		}
	}

	@Override
	public int count(Item item) {
		try(Transaction transaction = new Transaction(false)) {
			SetMatchingInsertable match = new SetMatchingInsertable(Collections.singleton(item), Integer.MAX_VALUE);
			this.participant.extract(transaction, match);
			return match.found.get(transaction);
		}
	}

	@Override
	public boolean containsAny(Set<Item> items) {
		try(Transaction transaction = new Transaction(false)) {
			SetMatchingInsertable match = new SetMatchingInsertable(items, 1);
			this.participant.extract(transaction, match);
			return match.isFull(null);
		}
	}

	@Override
	public void clear() {
		this.participant.clear(null);
	}

	private static final int[] AVAILABLE_SLOTS = new int[] {0, 1};
	@Override
	public int[] getAvailableSlots(Direction side) {
		return new int[0];
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return false;
	}
}

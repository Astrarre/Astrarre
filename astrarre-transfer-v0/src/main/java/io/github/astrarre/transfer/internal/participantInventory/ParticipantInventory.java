package io.github.astrarre.transfer.internal.participantInventory;

import java.util.Set;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.internal.inventory.FilteringInventory;
import io.github.astrarre.transfer.internal.access.ItemStackAccess;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
public class ParticipantInventory implements Inventory, FilteringInventory {
	// todo markDirty to validate NBT
	private static final Logger LOGGER = LogManager.getLogger("ParticipantInventory");
	private static final int BUFFER_EXTRACTION_SLOT = 0;
	private static final int BUFFER_INSERTION_SLOT = 1;
	private static int counter = 0;
	public final Participant<ItemKey> participant;
	private final InternalItemSlotParticipant slot = new InternalItemSlotParticipant();

	public ParticipantInventory(Participant<ItemKey> participant) {this.participant = participant;}

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
			try (Transaction transaction = Transaction.create(false)) {
				this.participant.extract(transaction, this.slot);
				// mutations need to be reflected in the participant
				final int oldCount = this.slot.getQuantity(transaction);
				final ItemKey item = this.slot.getType(transaction);
				ItemStack stack = item.createItemStack(oldCount);
				this.watchStack(item, stack);
				return stack;
			}
		} else {
			// 'buffer insertion slot'
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		this.test(slot);
		if (slot == BUFFER_EXTRACTION_SLOT) {
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

	private void test(int slot) {
		if (slot != BUFFER_EXTRACTION_SLOT && slot != BUFFER_INSERTION_SLOT) {
			throw new IndexOutOfBoundsException(slot + " for size 2!");
		}
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.test(slot);
		// if slot = 0, then they are trying to insert into the one slot we said to not insert in.
		// but, we'll let it pass
		ItemKey item = ItemKey.of(stack);
		this.watchStack(item, stack);
		this.participant.insert(null, item, stack.getCount());
	}

	/**
	 * allows mutations in the ItemStack to be reflected in the participant (nbt stuff still doesn't tho)
	 */
	private void watchStack(ItemKey item, ItemStack stack) {
		int[] lastCount = {stack.getCount()};
		ItemStackAccess.of(stack).astrarre_onChange(i -> {
			int count = i.getCount();
			int oldCount = lastCount[0];
			if (count == oldCount) {
				return;
			}

			try (Transaction commit = Transaction.create(true)) {
				int leftover = oldCount - this.participant.extract(commit, item, oldCount);
				int toInsert = count - leftover;
				boolean abort = false;
				if (toInsert > 0) {
					int rejected = this.participant.insert(commit, item, toInsert);
					if (rejected > 0) {
						abort = true;
					} else {
						lastCount[0] = count;
					}
				} else {
					abort = true;
				}

				if (abort) {
					// duplicated items, reject the stack count change
					commit.abort();
					stack.setCount(oldCount);
					if (counter % 10 == 0) {
						LOGGER.warn(
								"Denying ItemStack#setCount modification in an attempt to prevent duplication! (This warning is only logged every 10" +
								" times this happens)");
					}
					counter++;
				}
			}
		});
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {return true;}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		if (slot == BUFFER_EXTRACTION_SLOT) {
			return false;
		} else {
			try (Transaction transaction = Transaction.create(false)) {
				int count = stack.getCount();
				return this.participant.insert(transaction, ItemKey.of(stack), count) == count;
			}
		}
	}

	@Override
	public int count(Item item) {
		ItemStack stack = this.getStack(BUFFER_EXTRACTION_SLOT);
		return stack.getItem() == item ? stack.getCount() : 0;
	}

	@Override
	public boolean containsAny(Set<Item> items) {
		ItemStack stack = this.getStack(BUFFER_EXTRACTION_SLOT);
		return items.contains(stack.getItem());
	}

	private void reflectStack() {

	}

	@Override
	public void clear() {
		this.participant.clear(null);
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack) {
		return this.isValid(slot, stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack) {
		return slot == BUFFER_EXTRACTION_SLOT;
	}
}

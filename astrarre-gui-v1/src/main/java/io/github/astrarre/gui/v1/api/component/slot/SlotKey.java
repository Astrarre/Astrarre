package io.github.astrarre.gui.v1.api.component.slot;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.comms.PacketKey;
import io.github.astrarre.gui.v1.api.server.ServerPanel;
import io.github.astrarre.hash.v0.api.Hasher;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/**
 * @see ASlotHelper#linkFromServer(PacketHandler, ServerPanel, SlotKey)
 */
public class SlotKey extends PacketKey {
	private static final Id ID = Id.create("astrarre", "slot_key");

	final Inventory inventory;
	final int inventoryId;
	final int slotIndex;
	final List<SlotKey> quickTransfer = new ArrayList<>();

	/**
	 * @param inventory the inventory, does not actually have to be an updated view, this is primarily used for it's isValid method
	 * @param inventoryId a unique int id for this inventory, this is used to link the inventory to it's serverside counterpart
	 * @param slotIndex the slot (index in inventory) this slot represents
	 */
	protected SlotKey(Inventory inventory, int inventoryId, int slotIndex) {
		super(ID);
		this.inventory = inventory;
		this.inventoryId = inventoryId;
		this.slotIndex = slotIndex;
	}

	/**
	 * Allows the current slot to transfer items to the given slot when the user shift clicks the current slot
	 */
	public SlotKey link(SlotKey key) {
		this.quickTransfer.add(key);
		return this;
	}

	public SlotKey linkAll(Iterable<SlotKey> keys) {
		for(SlotKey key : keys) {
			this.quickTransfer.add(key);
		}
		return this;
	}

	public SlotKey linkPre(SlotKey key) {
		this.quickTransfer.add(0, key);
		return this;
	}

	public SlotKey linkAllPre(Iterable<SlotKey> keys) {
		int index = 0;
		for(SlotKey key : keys) {
			this.quickTransfer.add(index++, key);
		}
		return this;
	}

	public List<SlotKey> getKeys() {
		return this.quickTransfer;
	}

	public ItemStack getStack() {
		return this.inventory.getStack(this.slotIndex);
	}

	public void setStack(ItemStack stack) {
		this.inventory.setStack(this.slotIndex, stack);
	}

	public ItemStack transferToLinked() {
		ItemStack stack = this.getStack(); // real stack not render stack
		ItemKey item = ItemKey.ofStack(stack);
		int count = this.extract(item, stack.getCount(), false);
		for(SlotKey key : this.quickTransfer) {
			if(count == 0) break;
			count = count - key.insert(item, count, false);
		}
		return item.createItemStack(count);
	}

	/**
	 * Attempt to insert the given stack into the slot
	 *
	 * @param key can be mutated and returned
	 * @param simulate whether or not to actually take the itemstack out of the slot
	 * @return the amount actually inserted
	 */
	public int insert(ItemKey key, int count, boolean simulate) {
		if(count == 0 || key.isEmpty()) {
			return 0;
		}
		ItemStack current = this.getStack();
		if(current.isEmpty()) {
			if(!simulate) {
				this.setStack(key.createItemStack(count));
			}
			return count;
		} else if(key.isEqual(current)) {
			int toInsert = Math.min(current.getMaxCount() - current.getCount(), count);
			if(!simulate) {
				current.increment(toInsert);
			}
			return toInsert;
		} else {
			return 0;
		}
	}

	/**
	 * @return the amount actually extracted
	 */
	public int extract(ItemKey key, int count, boolean simulate) {
		if(count == 0 || key.isEmpty()) {
			return 0;
		}
		ItemStack current = this.getStack();
		if(!current.isEmpty() && key.isEqual(current)) {
			int toExtract = Math.min(current.getCount(), count);
			if(!simulate) {
				current.decrement(toExtract);
			}
			return toExtract;
		}
		return 0;
	}

	/**
	 * fired when the slot is changed
	 */
	public void markDirty() {
		this.inventory.markDirty();
	}

	/**
	 * @param key may be empty (when minecraft doesn't give us context)
	 * @return the maximum of the given itemstack that can exist in this slot
	 */
	public int getMaxCount(ItemKey key) {
		return this.inventory.getMaxCountPerStack();
	}

	@Override
	public String toString() {
		String name = this.inventory.getClass().getSimpleName();
		return "(InventoryName: " + name + ", InventoryId: " + this.inventory + ", SlotIndex" + this.slotIndex + ")";
	}

	@Override
	protected void hash0(Hasher hasher) {
		hasher.putInt(this.inventoryId);
		hasher.putInt(this.slotIndex);
	}
}

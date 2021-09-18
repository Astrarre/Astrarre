package io.github.astrarre.gui.v1.api.component.slot;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.internal.mixin.ScreenHandlerAccess;
import io.github.astrarre.gui.internal.slot.SlotAdapter;
import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.comms.PacketKey;
import io.github.astrarre.gui.v1.api.server.ServerPanel;
import io.github.astrarre.hash.v0.api.Hasher;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

/**
 * @see SlotKey#sync(PacketHandler, ServerPanel)
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
	public SlotKey(Inventory inventory, int inventoryId, int slotIndex) {
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
		ItemStack current = this.getStack();
		count = Math.min(Math.min(key.getMaxStackSize(), this.getMaxCount(key)) - current.getCount(), count);
		ItemStack copy = key.createItemStack(count);
		if(count <= 0 || key.isEmpty() || !this.isValid(copy)) {
			return 0;
		}

		if(current.isEmpty()) {
			if(!simulate) {
				this.setStack(copy);
			}
			return count;
		} else if(key.isEqual(current)) {
			if(!simulate) {
				copy.setCount(current.getCount() + count);
				this.setStack(copy);
			}
			return count;
		} else {
			return 0;
		}
	}

	public static void syncAll(PacketHandler handler, ServerPanel panel, List<SlotKey> keys) {
		for(SlotKey key : keys) {
			key.sync(handler, panel);
		}
	}

	/**
	 * This method must be called from the server init in {@link ServerPanel#openHandled(PlayerEntity, ServerPanel.ClientInit,
	 * ServerPanel.ServerInit)}. It links the slots on the server with the ones on the client, allowing the itemstacks in the inventory to be
	 * synchronized.
	 */
	public void sync(PacketHandler packet, ServerPanel panel) {
		ScreenHandler handler = panel.screenHandler();
		packet.sendInfo(this, builder -> {
			var mc = new SlotAdapter(this.inventory, this.slotIndex, this);
			((ScreenHandlerAccess) handler).callAddSlot(mc);
			builder.putInt("index", mc.id);
		});
	}

	public static List<SlotKey> player(PlayerEntity entity, int inventoryId) {
		return player(entity.getInventory(), inventoryId);
	}

	/**
	 * @return a list of slot keys, for a player's inventory ordered 0-36. Links hotbar and main inventory together for shift-click transfer.
	 */
	public static List<SlotKey> player(PlayerInventory inventory, int inventoryId) {
		List<SlotKey> hotbar = inv(inventory, 0, 9, inventoryId), main = inv(inventory, 9, 36, inventoryId);
		hotbar.forEach(key -> key.linkAll(main));
		main.forEach(key -> key.linkAll(hotbar));

		List<SlotKey> combined = new ArrayList<>(hotbar.size() + main.size());
		combined.addAll(hotbar);
		combined.addAll(main);
		return combined;
	}

	public static List<SlotKey> inv(Inventory inventory, int inventoryId) {
		return inv(inventory, 0, inventory.size(), inventoryId);
	}

	public static List<SlotKey> inv(Inventory inventory, int from, int len, int inventoryId) {
		List<SlotKey> keys = new ArrayList<>(inventory.size());
		for(int i = 0; i < len; i++) {
			keys.add(new SlotKey(inventory, inventoryId, i + from));
		}
		return keys;
	}

	protected ItemStack removeStack(int count) {
		return this.inventory.removeStack(this.slotIndex, count);
	}

	protected boolean isValid(ItemStack stack) {
		return this.inventory.isValid(this.slotIndex, stack);
	}

	/**
	 * @return the amount actually extracted
	 */
	public int extract(ItemKey key, int count, boolean simulate) {
		if(count <= 0 || key.isEmpty()) {
			return 0;
		}
		ItemStack current = this.getStack();
		if(!current.isEmpty() && key.isEqual(current)) {
			int toExtract = Math.min(current.getCount(), count);
			if(!simulate) {
				this.removeStack(toExtract);
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

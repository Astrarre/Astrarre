package insane_crafting;

import io.github.astrarre.access.v0.api.Access;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class InsaneInventory implements Inventory {
	public static final int SIZE = 1076;
	@SuppressWarnings("MismatchedReadAndWriteOfArray")
	private static final ItemStack[] NULL_INVENTORY = new ItemStack[SIZE];

	final ItemStack[] inventory = new ItemStack[SIZE];
	final Access<Runnable> change = new Access<>("insane_crafting", "insane_inv_change", a -> () -> a.forEach(Runnable::run));
	boolean empty = true;

	@Override
	public int size() {
		return this.inventory.length;
	}

	@Override
	public boolean isEmpty() {
		return this.empty;
	}

	@Override
	public ItemStack getStack(int slot) {
		ItemStack stack = this.inventory[slot];
		if(stack == null) return ItemStack.EMPTY;
		return stack;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack old = this.getStack(slot);
		int toTake = Math.min(old.getCount(), amount);
		if(toTake == 0) {
			return ItemStack.EMPTY;
		} else {
			ItemStack copy = old.copy();
			copy.setCount(toTake);

			old.decrement(toTake);
			if(old.isEmpty()) {
				this.inventory[slot] = null;
			}

			this.change.get().run();
			return copy;
		}
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.removeStack(slot, Integer.MAX_VALUE);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.inventory[slot] = stack;
		this.change.get().run();
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void onClose(PlayerEntity player) {
		var inv = player.getInventory();
		for(ItemStack stack : this.inventory) {
			if(stack != null && !stack.isEmpty()) {
				inv.offerOrDrop(stack);
			}
		}
		this.change.get().run();
	}

	@Override
	public void clear() {
		System.arraycopy(NULL_INVENTORY, 0, this.inventory, 0, this.inventory.length);
		this.change.get().run();
	}
}

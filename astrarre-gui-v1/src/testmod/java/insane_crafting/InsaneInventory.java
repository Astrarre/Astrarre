package insane_crafting;

import java.awt.Point;

import io.github.astrarre.access.v0.api.Access;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class InsaneInventory implements Inventory {
	public static final int SIZE = 1076;
	@SuppressWarnings("MismatchedReadAndWriteOfArray") private static final ItemStack[] NULL_INVENTORY = new ItemStack[SIZE];

	final ItemStack[] inventory = new ItemStack[SIZE];
	final Access<Runnable> change = new Access<>("insane_crafting", "insane_inv_change", a -> () -> {
		for(Runnable runnable : a) {
			runnable.run();
		}
	});
	int first = Integer.MAX_VALUE, last = SIZE - 1;
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
		if(stack == null) {
			return ItemStack.EMPTY;
		}
		return stack;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) { // todo update
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
				this.updateFirst(0, 0);
				// todo
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
		if(stack.isEmpty()) {
			this.inventory[slot] = null;
		} else {
			this.inventory[slot] = stack;
		}
		this.change.get().run();
		this.updateFirst(slot, slot);
	}

	@Override
	public int getMaxCountPerStack() {
		return 1; // easier on automation
	}

	@Override
	public void markDirty() {
		this.updateFirst(0, SIZE - 1);
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

	public void updateFirst(int from, int to) {
		if(this.first < from) {
			return;
		}

		this.first = Integer.MAX_VALUE;

		for(int i = from; i < this.inventory.length; i++) {
			ItemStack at = this.inventory[i];
			if(!(at == null || at.isEmpty())) {
				this.first = i;
				break;
			}
		}


		for(int i = Math.max(this.last, to); i >= this.first; i--) {
			ItemStack at = this.inventory[i];
			if(!(at == null || at.isEmpty())) {
				this.last = i;
				return;
			}
		}

		this.last = SIZE - 1;
	}

	public static Point pointFromIndex(int index) {
		if(index < 936) {
			return new Point(index % 39, index / 39);
		} else if(index < 1006) {
			int norm = index - 936;
			int x = norm % 14, y = norm / 14;
			return new Point(x, y + 24);
		} else {
			int norm = index - 1006;
			int x = norm % 14, y = norm / 14;
			return new Point(x + 25, y + 24);
		}
	}

	public static int indexFromPoint(int x, int y) {
		int index = y * 39 + x;
		if(index < 936) {
			return index;
		} else if(x < 14) {
			return ((y - 24) * 14 + x) + 936;
		} else if(x >= 25) {
			return ((y - 24) * 14 + (x - 25)) + 1006;
		} else {
			return -1;
		}
	}

	@Override
	public void clear() {
		System.arraycopy(NULL_INVENTORY, 0, this.inventory, 0, this.inventory.length);
		this.change.get().run();
	}
}

package io.github.astrarre.transfer.internal.compat;

import java.util.AbstractList;
import java.util.List;
import java.util.function.ToIntFunction;

import io.github.astrarre.itemview.internal.access.ImmutableAccess;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.api.ReplacingParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.DiffKey;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import io.github.astrarre.util.v0.api.collection.ExposedDefaultList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class ShulkerboxItemParticipant implements Participant<ItemKey> {
	public final ReplacingParticipant<ItemKey> container;
	public final ShulkerBoxBlockEntity shulkerbox;
	public final ShulkerKey key = new ShulkerKey();
	public final Key.Object<ItemKey> currentKey;

	public ShulkerboxItemParticipant(ReplacingParticipant<ItemKey> container, ItemKey key, ShulkerBoxBlockEntity shulkerbox) {
		this.container = container;
		this.shulkerbox = shulkerbox;
		shulkerbox.readInventoryNbt(key.getTag().getTag("BlockEntityTag").toTag());
		this.currentKey = new ObjectKeyImpl<>(key);
	}

	public static Participant<ItemKey> create(ReplacingParticipant<ItemKey> container, ItemKey key, BlockEntityType<? extends ShulkerBoxBlockEntity> type) {
		ShulkerBoxBlockEntity shulkerbox = type.instantiate(BlockPos.ORIGIN, Blocks.SHULKER_BOX.getDefaultState());
		if (shulkerbox == null) {
			return Participants.EMPTY.cast();
		}
		return new ShulkerboxItemParticipant(container, key, shulkerbox);
	}

	@Override
	public void extract(@Nullable Transaction transaction, Insertable<ItemKey> insertable) {
		if (insertable.isFull(transaction)) {
			return;
		}
		this.operate(value -> {
			List<ItemStack> stacks = this.key.get(value);
			for (int i = 0; i < stacks.size(); i++) {
				ItemStack stack = stacks.get(i);
				int inserted = insertable.insert(value, ItemKey.of(stack), stack.getCount());
				if (inserted > 0) {
					stack = stack.copy();
					stack.decrement(inserted);
					stacks.set(i, stack);
				}
			}
			return 0;
		});
	}

	@Override
	public int extract(@Nullable Transaction transaction, @NotNull ItemKey type, int quantity) {
		if (ItemKey.EMPTY == type) {
			return 0;
		}
		return this.operate(value -> {
			List<ItemStack> stacks = this.key.get(value);
			int taken = 0;
			for (int i = 0; i < stacks.size(); i++) {
				ItemStack stack = stacks.get(i);
				if (type.isEqual(stack)) {
					int toTake = Math.min(stack.getCount(), quantity);
					stack = stack.copy();
					stack.decrement(toTake);
					stacks.set(i, stack);
					taken += toTake;
				}
			}
			return taken;
		});
	}

	protected int operate(ToIntFunction<Transaction> action) {
		try (Transaction transaction = Transaction.create()) {
			int val = action.applyAsInt(transaction);

			ItemKey current = this.currentKey.get(transaction);

			NbtCompound tag = new NbtCompound();
			Inventories.writeNbt(tag, new ExposedDefaultList<>(this.key.get(transaction), ItemStack.EMPTY), false);
			((ImmutableAccess) tag).astrarre_setImmutable();
			ItemKey withNewTag = current.withTag(FabricViews.immutableView(tag));

			if (this.container.replace(transaction, current, 1, withNewTag, 1)) {
				this.currentKey.set(transaction, withNewTag);
				return val;
			} else {
				transaction.abort();
				return 0;
			}
		}
	}

	@Override
	public int insert(@Nullable Transaction transaction, @NotNull ItemKey type, int quantity) {
		return this.operate(value -> {
			int q = quantity;
			List<ItemStack> stacks = this.key.get(value);
			int taken = 0;
			for (int i = 0; i < stacks.size(); i++) {
				ItemStack stack = stacks.get(i);
				if (type.isEqual(stack) || stack.isEmpty()) {
					int toInsert = Math.min(type.getMaxStackSize() - stack.getCount(), q);
					q -= toInsert;
					stacks.set(i, type.createItemStack(toInsert + stack.getCount()));
					taken += toInsert;
				}
			}
			return taken;
		});
	}

	protected class ShulkerList extends AbstractList<ItemStack> {
		@Override
		public int size() {
			return ShulkerboxItemParticipant.this.shulkerbox.size();
		}

		@Override
		public ItemStack get(int index) {
			return ShulkerboxItemParticipant.this.shulkerbox.getStack(index);
		}

		@Override
		public ItemStack set(int index, ItemStack element) {
			ItemStack stack = this.get(index);
			ShulkerboxItemParticipant.this.shulkerbox.setStack(index, element);
			return stack;
		}
	}

	protected class ShulkerKey extends DiffKey.Array<ItemStack> {
		protected ShulkerList root = new ShulkerList();

		public ShulkerKey() {
		}

		@Override
		protected List<ItemStack> getRootValue() {
			return this.root;
		}

		@Override
		protected void setRootValue(List<ItemStack> val) {
			this.root = (ShulkerList) val;
		}
	}
}

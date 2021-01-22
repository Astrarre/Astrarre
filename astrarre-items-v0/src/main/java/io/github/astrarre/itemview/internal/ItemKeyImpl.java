package io.github.astrarre.itemview.internal;

import java.util.Objects;

import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.item.Item;
import io.github.astrarre.v0.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;


public class ItemKeyImpl implements ItemKey {
	private final NBTagView nbtTag;
	private final net.minecraft.item.Item item;
	private final int hashCode;

	public ItemKeyImpl(Item item, NBTagView tag) {
		this((net.minecraft.item.Item) item, tag.copy());
	}

	public ItemKeyImpl(Item item, CompoundTag tag) {
		this((net.minecraft.item.Item) item, NBTagView.of(tag));
	}

	@Hide
	public ItemKeyImpl(net.minecraft.item.Item item, NBTagView tag) {
		this.item = item;
		this.nbtTag = tag == null ? null : tag.copy();

		int result = Objects.hashCode(tag);
		result = 31 * result + Objects.hashCode(item);
		this.hashCode = result;
	}

	@Hide
	public ItemKeyImpl(net.minecraft.item.Item item, net.minecraft.nbt.CompoundTag tag) {
		this(item, FabricViews.immutableView(tag));
	}

	@NotNull
	@Override
	public NBTagView getTag() {
		return this.nbtTag;
	}

	@Override
	public int getMaxStackSize() {
		// todo CompoundTag aware max count
		return this.asItem().getMaxCount();
	}

	@Override
	public net.minecraft.item.Item asItem() {
		return this.item == null ? Items.AIR : this.item;
	}

	@Override
	public ItemStack createItemStack(int count) {
		ItemStack stack = new ItemStack(this.asItem(), count);
		stack.setTag(this.getCompoundTag());
		return stack;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof ItemKeyImpl)) {
			return false;
		}

		ItemKeyImpl key = (ItemKeyImpl) object;

		if (!Objects.equals(this.nbtTag, key.nbtTag)) {
			return false;
		}
		return Objects.equals(this.item, key.item);
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}
}

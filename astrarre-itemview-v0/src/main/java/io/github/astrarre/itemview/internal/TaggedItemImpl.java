package io.github.astrarre.itemview.internal;

import java.util.Objects;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import org.jetbrains.annotations.NotNull;

import net.minecraft.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;

public class TaggedItemImpl implements ItemKey {
	private final NBTagView nbtTag;
	private final Item item;
	private final int hashCode;

	public TaggedItemImpl(Item item, NBTagView tag) {
		if(item == null) {
			Bootstrap.initialize();
			item = Items.AIR;
		}
		this.item = item;
		this.nbtTag = tag == null ? NBTagView.EMPTY : tag.copy();

		int result = Objects.hashCode(tag);
		result = 31 * result + Objects.hashCode(item);
		this.hashCode = result;
	}

	public TaggedItemImpl(Item item, CompoundTag tag) {
		this(item, FabricViews.immutableView(tag));
	}

	@NotNull
	@Override
	public NBTagView getTag() {
		return this.nbtTag;
	}

	@Override
	public int getMaxStackSize() {
		if(this.nbtTag.isEmpty()) {
			return this.getItem().getMaxCount();
		} else {
			ItemStack stack = new ItemStack(this.getItem());
			stack.setTag(this.nbtTag.copyTag());
			return stack.getMaxCount();
		}
	}

	@Override
	public Item getItem() {
		return this.item == null ? Items.AIR : this.item;
	}

	@Override
	public ItemStack createItemStack(int count) {
		if(count == 0) return ItemStack.EMPTY;
		ItemStack stack = new ItemStack(this.getItem(), count);
		stack.setTag(this.getCompoundTag());
		return stack;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof TaggedItemImpl)) {
			return false;
		}

		TaggedItemImpl key = (TaggedItemImpl) object;

		if (!Objects.equals(this.nbtTag, key.nbtTag)) {
			return false;
		}
		return Objects.equals(this.item, key.item);
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.item.toString());
		if(!this.nbtTag.isEmpty()) {
			builder.append(' ').append(this.nbtTag);
		}
		return builder.toString();
	}
}

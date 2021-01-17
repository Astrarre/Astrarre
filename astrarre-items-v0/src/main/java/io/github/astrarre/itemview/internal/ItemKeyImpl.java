package io.github.astrarre.itemview.internal;

import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.item.Item;
import io.github.astrarre.v0.item.ItemStack;

import net.minecraft.nbt.CompoundTag;


public class ItemKeyImpl implements ItemKey {
	private final NBTagView nbtTag;
	private final net.minecraft.item.Item item;

	public ItemKeyImpl(Item item, NBTagView tag) {
		this.item = (net.minecraft.item.Item) item;
		this.nbtTag = tag;
	}

	public ItemKeyImpl(Item item, NBTagView tag) {
		this.item = (net.minecraft.item.Item) item;
		this.nbtTag = tag;
	}

	@Hide
	public ItemKeyImpl(net.minecraft.item.Item item, NBTagView tag) {
		this.item = item;
		this.nbtTag = tag;
	}

	@Hide
	public ItemKeyImpl(net.minecraft.item.Item item, CompoundTag tag) {
		this.item = item;
		this.nbtTag = FabricViews.immutableView(tag);
	}

	@Override
	public NBTagView getTag() {
		return this.nbtTag;
	}

	@Override
	public net.minecraft.item.Item asItem() {
		return this.item;
	}

	@Override
	public ItemStack createItemStack(int count) {
		return null;
	}
}

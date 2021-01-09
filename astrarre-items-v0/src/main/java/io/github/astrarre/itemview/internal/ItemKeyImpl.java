package io.github.astrarre.itemview.internal;

import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.stripper.Hide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemKeyImpl implements ItemKey {
	private final Item item;
	@NotNull
	private final NBTagView tag;

	/**
	 * @see #toTag(int)
	 */
	private NBTagView lazyTag;

	@Hide
	public ItemKeyImpl(@Nullable Item item, @Nullable CompoundTag tag) {
		this.tag = FabricViews.immutableView(tag);
		this.item = item;
	}

	public ItemKeyImpl(@Nullable Item item, @NotNull NBTagView tag) {
		this.tag = FabricViews.immutable(tag);
		this.item = item;
	}

	@Override
	public boolean isEmpty() {
		return this.item == Items.AIR;
	}

	@Override
	public Item getItem() {
		return this.item;
	}

	@Override
	public NBTagView toTag(int count) {
		NBTagView toTag = this.lazyTag;
		if (toTag == null) {
			CompoundTag tag = new CompoundTag();
			Identifier identifier = Registry.ITEM.getId(this.getItem());
			tag.putString("id", identifier.toString());
			tag.putByte("Count", (byte) count);
			tag.put("tag", FabricViews.from(this.tag));
			this.lazyTag = toTag = FabricViews.view(tag);
		}
		return toTag;
	}

	@Override
	public int getMaxCount() {
		// todo support stack aware durabilities?
		return this.item.getMaxCount();
	}

	@Override
	public boolean areTagsEqual(ItemKey view) {
		return this.tag.equals(view.getTag());
	}

	@Override
	public boolean canStackWith(ItemKey view) {
		return view.getItem() == this.item && this.areTagsEqual(view);
	}

	@Override
	public boolean hasTag() {
		return this.tag == NBTagView.EMPTY;
	}

	@Override
	public @NotNull NBTagView getTag() {
		return this.tag;
	}

	@Override
	public @Nullable NBTagView getSubTag(String key) {
		return this.tag.getTag(key);
	}
}

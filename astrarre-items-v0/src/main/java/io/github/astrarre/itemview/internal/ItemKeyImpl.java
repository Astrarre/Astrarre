package io.github.astrarre.itemview.internal;

import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.v0.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemKeyImpl implements ItemKey {
	private final Item item;
	private final int count;
	@Nullable
	private final NBTagView tag;

	/**
	 * @see #toTag()
	 */
	private NBTagView lazyTag;

	public ItemKeyImpl(@Nullable Item item, int count, @Nullable NBTagView tag) {
		if (count < 0) {
			throw new IllegalArgumentException("Count cannot be negative (" + count + ")");
		}
		if (item == null || count == 0) {
			item = Items.AIR;
		}

		this.tag = FabricItemViews.immutable(tag);
		this.item = item;
		this.count = count;
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
	public NBTagView toTag() {
		NBTagView toTag = this.lazyTag;
		if (toTag == null) {
			CompoundTag tag = new CompoundTag();
			Identifier identifier = Registry.ITEM.getId(this.getItem());
			tag.putString("id", identifier.toString());
			tag.putByte("Count", (byte) this.count);
			if (this.tag != null) {
				tag.put("tag", FabricItemViews.from(this.tag));
			}
			this.lazyTag = toTag = FabricItemViews.view(tag);
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
		if (this.tag == null) {
			return view == null;
		} else {
			return this.tag.equals(view.getTag());
		}
	}

	@Override
	public boolean canStackWith(ItemKey view) {
		return view.getItem() == this.item && this.areTagsEqual(view);
	}

	@Override
	public boolean hasTag() {
		return this.tag != null;
	}

	@Override
	public @Nullable NBTagView getTag() {
		return this.tag;
	}

	@Override
	public @Nullable NBTagView getSubTag(String key) {
		return this.tag == null ? null : this.tag.getTag(key);
	}
}

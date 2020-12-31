package io.github.astrarre.itemview.v0.api.item;

import io.github.astrarre.itemview.internal.FabricItemViews;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;

public interface ItemKey {
	// todo abstract IItem
	boolean isEmpty();

	@Hide
	default io.github.astrarre.v0.item.Item getKey() {
		return (io.github.astrarre.v0.item.Item) this.getItem();
	}

	Item getItem();

	/**
	 * @return serializes the ItemView
	 * @see FabricItemViews#fromTag(NBTagView)
	 */
	NBTagView toTag();

	/**
	 * @return the maximum the ItemStack (that this ItemView represents) can stack to
	 */
	int getMaxCount();

	/**
	 * @return true if the nbt tags are equal to each other
	 */
	boolean areTagsEqual(ItemKey view);

	default boolean areTagsEqual(ItemStack stack) {
		return this.areTagsEqual((ItemKey) stack);
	}

	@Hide
	default boolean areTagsEqual(net.minecraft.item.ItemStack stack) {
		return this.areTagsEqual((ItemStack) (Object) stack);
	}

	/**
	 * @return true if the nbt tags and items are equal, it does not check max stack size!
	 */
	boolean canStackWith(ItemKey view);

	default boolean canStackWith(io.github.astrarre.v0.item.ItemStack stack) {
		return this.canStackWith((ItemKey) stack);
	}

	@Hide
	default boolean canStackWith(net.minecraft.item.ItemStack stack) {
		return this.canStackWith((ItemKey) (Object) stack);
	}

	/**
	 * @return true if the ItemStack has nbt data
	 */
	boolean hasTag();

	/**
	 * @return the nbt data of the ItemStack
	 */
	@Nullable
	NBTagView getTag();

	/**
	 * @return the nbt tag at a given key
	 */
	@Nullable
	NBTagView getSubTag(String key);

	@NotNull
	default NBTagView getOrNewTag() {
		if(this.hasTag()) {
			return this.getTag();
		} else {
			return FabricItemViews.view(new CompoundTag());
		}
	}
}

package io.github.astrarre.itemview.v0.api.item;

import io.github.astrarre.itemview.internal.FabricViews;
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

	default io.github.astrarre.v0.item.Item getKey() {
		return (io.github.astrarre.v0.item.Item) this.getItem();
	}

	@Hide
	Item getItem();

	/**
	 * @return serializes the ItemView
	 * @see FabricViews#fromTag(NBTagView)
	 */
	NBTagView toTag(int count);

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
	 * {@code hasTag == EMPTY}
	 * @return true if the ItemStack has nbt data
	 */
	default boolean hasTag() {
		return this.getTag() == NBTagView.EMPTY;
	}

	/**
	 * @return the nbt data of the ItemStack
	 */
	@NotNull
	NBTagView getTag();

	/**
	 * @return the nbt tag at a given key
	 */
	@Nullable
	NBTagView getSubTag(String key);
}

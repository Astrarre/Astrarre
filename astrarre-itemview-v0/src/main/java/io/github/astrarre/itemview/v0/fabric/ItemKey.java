package io.github.astrarre.itemview.v0.fabric;

import io.github.astrarre.itemview.internal.TaggedItemImpl;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import org.jetbrains.annotations.NotNull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;

/**
 * an Item and it's NBT data (guaranteed immutable)
 */
public interface ItemKey {
	Serializer<ItemKey> SERIALIZER = Serializer.of(input -> of(FabricSerializers.ITEM_STACK.read(input)), (instance) -> FabricSerializers.ITEM_STACK.save(instance.createItemStack(1)));

	static ItemKey of(ItemStack stack) {
		if (stack.isEmpty()) {
			return EMPTY;
		} else if (stack.hasTag()) {
			return new TaggedItemImpl(stack.getItem(), stack.getTag());
		} else {
			return of(stack.getItem());
		}
	}

	static ItemKey of(Item item) {
		return (ItemKey) item;
	}

	static ItemKey of(Item item, NBTagView view) {
		return new TaggedItemImpl(item, FabricViews.immutableView(view.toTag()));
	}

	ItemKey EMPTY = new TaggedItemImpl(Items.AIR, NBTagView.EMPTY);

	// todo cache ItemKey with versioning for mutable array tags

	default ItemStack createItemStack(int count) {
		if(count == 0) return ItemStack.EMPTY;
		ItemStack stack = new ItemStack(this.getItem(), count);
		stack.setTag(this.getTag().copyTag());
		return stack;
	}

	Item getItem();

	/**
	 * @return an immutable view of the ItemKey
	 */
	@NotNull
	NBTagView getTag();

	default CompoundTag getCompoundTag() {
		return this.getTag().copyTag();
	}

	/**
	 * @return true if the item and compound tags are equal
	 */
	default boolean isEqual(ItemStack stack) {
		return stack.getItem() == this.getItem() && this.getTag().equals(stack.getTag());
	}

	/**
	 * @return true if `count` == ItemStack#getCount and the items and compound tags are equal
	 */
	default boolean isEqual(int count, ItemStack stack) {
		return count == stack.getCount() && this.isEqual(stack);
	}
	/**
	 * @return the maximum stack size of the item
	 */
	int getMaxStackSize();

	default boolean isEmpty() {return this == EMPTY;}

	default ItemKey withTag(NBTagView n) {
		if (n.isEmpty()) {
			return (ItemKey) this.getItem();
		}

		if(this.getItem() == Items.AIR) {
			return (ItemKey) Items.AIR;
		}

		return new TaggedItemImpl(this.getItem(), FabricViews.immutableView(n.toTag()));
	}
}

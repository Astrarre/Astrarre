package io.github.astrarre.itemview.internal;

import java.util.List;

import io.github.astrarre.itemview.internal.access.AbstractListTagAccess;
import io.github.astrarre.itemview.v0.api.item.ItemStackView;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongList;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

/**
 * A utility class for fabric mods
 */
@SuppressWarnings ("ConstantConditions")
public class FabricItemViews {
	// todo change to immutable when possible

	/**
	 * @return create an immutable itemstack of an item (amount = 1)
	 */
	public static ItemStackView create(ItemConvertible item) {
		return create(item, 1);
	}

	/**
	 * @return creates an immutable itemstack of the item and it's amount
	 */
	public static ItemStackView create(ItemConvertible item, int amount) {
		return view(new ItemStack(item, amount));
	}

	/**
	 * @return an unmodifiable ItemStack view
	 */
	public static ItemStackView view(ItemStack stack) {
		return (ItemStackView) (Object) stack;
	}

	/**
	 * @return read and create a new immutable ItemView from a tag
	 */
	public static ItemStackView fromTag(NBTagView view) {
		return view(ItemStack.fromTag(fromUnsafe(view)));
	}

	/**
	 * @see #from(NBTagView)
	 * @deprecated unsafe
	 */
	@Deprecated
	public static CompoundTag fromUnsafe(NBTagView view) {
		// todo check for ImmutableCompoundTag when that is implemented
		return (CompoundTag) view;
	}

	/**
	 * @return an immutable ItemStack view
	 */
	public static ItemStackView immutableView(ItemStack stack) {
		if (stack == null) {
			return ItemStackView.EMPTY;
		}
		return view(stack.copy());
	}

	/**
	 * @return an immutable copy of the ItemView
	 */
	public static ItemStackView immutable(ItemStackView view) {
		return view(from(view));
	}

	/**
	 * @return copy an ItemView to an ItemStack
	 */
	public static ItemStack from(ItemStackView view) {
		return fromUnsafe(view).copy();
	}

	/**
	 * @see #from(ItemStackView)
	 * @deprecated unsafe
	 */
	@Deprecated
	public static ItemStack fromUnsafe(ItemStackView view) {
		// todo check for ImmutableItemStack when that is implemented
		return (ItemStack) (Object) view;
	}

	/**
	 * @return an immutable compound tag view
	 */
	public static NBTagView immutableView(CompoundTag tag) {
		return (NBTagView) tag.copy();
	}

	public static NBTagView immutable(NBTagView view) {
		return view(from(view));
	}

	/**
	 * @return an unmodifiable compound tag view
	 */
	public static NBTagView view(CompoundTag tag) {
		return (NBTagView) tag;
	}

	/**
	 * @return the NBTagView converted to a compound tag
	 */
	public static CompoundTag from(NBTagView view) {
		return fromUnsafe(view).copy();
	}

	public static <T> T immutableView(Tag tag, NBTType<T> type) {
		return view(tag.copy(), type);
	}

	@SuppressWarnings ("unchecked")
	public static <T> T view(Tag tag, NBTType<T> type) {
		Object ret = null;
		if (tag instanceof AbstractNumberTag) {
			Number number = ((AbstractNumberTag) tag).getNumber();
			if (type == NBTType.BOOL) {
				ret = number.byteValue() != 0;
			} else if (type == NBTType.CHAR) {
				ret = (char) number.shortValue();
			} else {
				ret = number;
			}
		} else if (tag instanceof AbstractListTagAccess) {
			ret = ((AbstractListTagAccess) tag).itemview_getListTag(type);
		} else if (tag instanceof CompoundTag) {
			// compound tag implements NBTagView, shhh
			ret = tag;
		} else if (tag instanceof StringTag) {
			ret = tag.asString();
		}

		if (ret == null) {
			throw new IllegalArgumentException("unknown tag type " + tag + "(" + tag.getClass() + ")");
		}

		if (type.getClassType().isInstance(ret)) {
			return (T) ret;
		} else {
			throw new ClassCastException(tag.getClass() + " != " + type.getClassType());
		}
	}

	// todo from methods

	public static <T> List<T> immutableView(ListTag tag, NBTType<T> componentType) {
		return view(tag.copy(), componentType);
	}

	public static <T> List<T> view(ListTag tags, NBTType<T> componentType) {
		return (List<T>) ((AbstractListTagAccess) tags).itemview_getListTag(componentType);
	}

	public static ByteList immutableView(ByteArrayTag tag) {
		return view((ByteArrayTag) tag.copy());
	}

	public static ByteList view(ByteArrayTag tags) {
		return (ByteList) ((AbstractListTagAccess) tags).itemview_getListTag(NBTType.BYTE_ARRAY);
	}

	public static IntList immutableView(IntArrayTag tag) {
		return view(tag.copy());
	}

	public static IntList view(IntArrayTag tags) {
		return (IntList) ((AbstractListTagAccess) tags).itemview_getListTag(NBTType.INT_ARRAY);
	}

	public static LongList immutableView(LongArrayTag tag) {
		return view(tag.copy());
	}

	public static LongList view(LongArrayTag tags) {
		return (LongList) ((AbstractListTagAccess) tags).itemview_getListTag(NBTType.LONG_ARRAY);
	}
}

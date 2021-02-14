package io.github.astrarre.itemview.internal;

import java.util.List;

import io.github.astrarre.itemview.internal.access.AbstractListTagAccess;
import io.github.astrarre.itemview.internal.access.ImmutableAccess;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public class FabricViews {
	// todo change to immutable when possible
	// todo store immutable status somewhere

	/**
	 * @return an immutable compound tag view
	 */
	@NotNull
	public static NBTagView immutableView(@Nullable CompoundTag tag) {
		if (tag == null || tag.isEmpty()) {
			return NBTagView.EMPTY;
		}

		if(((ImmutableAccess)tag).astrarre_isImmutable()) {
			return (NBTagView) tag;
		}

		NBTagView view = (NBTagView) tag.copy();
		((ImmutableAccess)view).astrarre_setImmutable();
		return view;
	}

	/**
	 * @return an unmodifiable compound tag view
	 */
	@NotNull
	public static NBTagView view(@NotNull CompoundTag tag) {
		return (tag == null || tag.isEmpty()) ? NBTagView.EMPTY : (NBTagView) tag;
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

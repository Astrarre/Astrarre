package io.github.astrarre.itemview.v0.fabric;

import java.util.List;

import com.google.gson.internal.Primitives;
import io.github.astrarre.itemview.internal.access.AbstractListTagAccess;
import io.github.astrarre.itemview.internal.access.ImmutableAccess;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;

@SuppressWarnings ("ConstantConditions")
public class FabricViews {
	/**
	 * @return an immutable compound tag view
	 */
	@NotNull
	public static NBTagView immutableView(@Nullable NbtCompound tag) {
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
	public static NBTagView view(@Nullable NbtCompound tag) {
		return (tag == null || tag.isEmpty()) ? NBTagView.EMPTY : (NBTagView) tag;
	}

	public static <T> T immutableView(NbtElement tag, NBTType<T> type) {
		return view(tag.copy(), type);
	}

	@SuppressWarnings ("unchecked")
	public static <T> T view(NbtElement tag, @Nullable NBTType<T> type) {
		Object ret = null;
		if (tag instanceof AbstractNbtNumber) {
			Number number = ((AbstractNbtNumber) tag).numberValue();
			if (type == NBTType.BOOL) {
				ret = number.byteValue() != 0;
			} else if (type == NBTType.CHAR) {
				ret = (char) number.shortValue();
			} else {
				ret = number;
			}
		} else if (tag instanceof AbstractListTagAccess) {
			ret = ((AbstractListTagAccess) tag).itemview_getListTag(type);
		} else if (tag instanceof NbtCompound) {
			// compound tag implements NBTagView, shhh
			ret = tag;
		} else if (tag instanceof NbtString) {
			ret = tag.asString();
		} else if(tag == null) {
			return null;
		}

		if (ret == null) {
			throw new IllegalArgumentException("unknown tag type " + tag + "(" + tag.getClass() + ")");
		}


		if (type == null || Primitives.wrap(type.getClassType()).isInstance(ret)) {
			return (T) ret;
		} else {
			throw new ClassCastException((ret == null ? null : ret.getClass()) + " != " + type.getClassType());
		}
	}


	public static NbtElement from(Object object) {
		if(object instanceof Boolean) {
			return NbtByte.of((Boolean)object);
		} else if(object instanceof Byte) {
			return NbtByte.of((Byte) object);
		} else if(object instanceof Character) {
			return NbtShort.of((short) ((Character)object).charValue());
		} else if(object instanceof Short) {
			return NbtShort.of((Short) object);
		} else if(object instanceof Float) {
			return NbtFloat.of((Float) object);
		} else if(object instanceof Integer) {
			return NbtInt.of((Integer) object);
		} else if(object instanceof Double) {
			return NbtDouble.of((Double) object);
		} else if(object instanceof Long) {
			return NbtLong.of((Long) object);
		} else if(object instanceof NBTagView) {
			return ((NBTagView) object).copyTag();
		} else if(object instanceof IntList) {
			return new NbtIntArray(((IntList) object).toIntArray());
		} else if(object instanceof ByteList) {
			return new NbtByteArray(((ByteList) object).toByteArray());
		} else if(object instanceof LongList) {
			return new NbtLongArray(((LongList) object).toLongArray());
		} else if(object instanceof List) {
			List<?> objects = (List<?>) object;
			NbtList tag = new NbtList();
			for (Object o : objects) {
				tag.add(from(o));
			}
			return tag;
		} else if(object instanceof String) {
			return NbtString.of((String) object);
		}
		throw new UnsupportedOperationException(object + "");
	}

	public static <T> List<T> immutableView(NbtList tag, NBTType<T> componentType) {
		return view(tag.copy(), componentType);
	}

	public static <T> List<T> view(NbtList tags, NBTType<T> componentType) {
		return (List<T>) ((AbstractListTagAccess) tags).itemview_getListTag(componentType);
	}

	public static ByteList immutableView(NbtByteArray tag) {
		return view((NbtByteArray) tag.copy());
	}

	public static ByteList view(NbtByteArray tags) {
		return (ByteList) ((AbstractListTagAccess) tags).itemview_getListTag(NBTType.BYTE_ARRAY);
	}

	public static IntList immutableView(NbtIntArray tag) {
		return view(tag.copy());
	}

	public static IntList view(NbtIntArray tags) {
		return (IntList) ((AbstractListTagAccess) tags).itemview_getListTag(NBTType.INT_ARRAY);
	}

	public static LongList immutableView(NbtLongArray tag) {
		return view(tag.copy());
	}

	public static LongList view(NbtLongArray tags) {
		return (LongList) ((AbstractListTagAccess) tags).itemview_getListTag(NBTType.LONG_ARRAY);
	}
}

package io.github.astrarre.itemview.v0.fabric;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.astrarre.itemview.internal.access.AbstractListTagAccess;
import io.github.astrarre.itemview.internal.access.ImmutableAccess;
import io.github.astrarre.itemview.internal.mixin.nbt.CompoundTagAccess;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.io.AbstractTag;
import net.minecraft.util.io.ByteArrayTag;
import net.minecraft.util.io.ByteTag;
import net.minecraft.util.io.CompoundTag;
import net.minecraft.util.io.DoubleTag;
import net.minecraft.util.io.FloatTag;
import net.minecraft.util.io.IntTag;
import net.minecraft.util.io.ListTag;
import net.minecraft.util.io.LongTag;
import net.minecraft.util.io.ShortTag;
import net.minecraft.util.io.StringTag;

@SuppressWarnings ("ConstantConditions")
public class FabricViews {
	public static boolean isEmpty(CompoundTag tag) {
		return tag == null || tag.values().isEmpty();
	}

	public static AbstractTag clone(AbstractTag tag) {
		AbstractTag val = null;
		if(tag instanceof CompoundTag) {
			Map<String, AbstractTag> data = ((CompoundTagAccess)tag).getData();
			CompoundTag clone = new CompoundTag();
			for (Map.Entry<String, AbstractTag> entry : data.entrySet()) {
				String key = entry.getKey();
				AbstractTag t = entry.getValue();
				clone.put(key, clone(t));
			}
			val = clone;
		} else if(tag instanceof ByteTag) {
			val = new ByteTag(((ByteTag) tag).data);
		} else if(tag instanceof ByteArrayTag) {
			val = new ByteArrayTag(((ByteArrayTag) tag).data.clone());
		} else if(tag instanceof DoubleTag) {
			val = new DoubleTag(((DoubleTag) tag).data);
		} else if(tag instanceof FloatTag) {
			val = new FloatTag(((FloatTag) tag).data);
		} else if(tag instanceof IntTag) {
			val = new IntTag(((IntTag) tag).data);
		} else if(tag instanceof ListTag) {
			ListTag copy = new ListTag();
			ListTag casted = (ListTag) tag;
			for (int i = 0; i < ((ListTag) tag).size(); i++) {
				copy.add(clone(casted.get(i)));
			}
			val = copy;
		} else if(tag instanceof LongTag) {
			val = new LongTag(((LongTag) tag).data);
		} else if(tag instanceof ShortTag) {
			val = new ShortTag(((ShortTag) tag).data);
		} else if(tag instanceof StringTag) {
			val = new StringTag(((StringTag) tag).data);
		}

		val.setType(tag.getType());
		return val;
	}

	/**
	 * @return an immutable compound tag view
	 */
	@NotNull
	public static NBTagView immutableView(@Nullable CompoundTag tag) {
		if (tag == null || tag.values().isEmpty()) {
			return NBTagView.EMPTY;
		}

		if(((ImmutableAccess)tag).astrarre_isImmutable()) {
			return (NBTagView) tag;
		}

		NBTagView view = (NBTagView) clone(tag);
		((ImmutableAccess)view).astrarre_setImmutable();
		return view;
	}

	/**
	 * @return an unmodifiable compound tag view
	 */
	@NotNull
	public static NBTagView view(@Nullable CompoundTag tag) {
		return isEmpty(tag) ? NBTagView.EMPTY : (NBTagView) tag;
	}

	public static <T> T immutableView(AbstractTag tag, NBTType<T> type) {
		return view(clone(tag), type);
	}

	@SuppressWarnings ("unchecked")
	public static <T> T view(AbstractTag tag, @Nullable NBTType<T> type) {
		Object ret = null;
		if(tag instanceof ByteTag) {
			if(type == NBTType.BOOL) {
				return (T) Boolean.valueOf(((ByteTag) tag).data == 1);
			}
			return ((ByteTag) tag).data;
		} else if(tag instanceof )
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
			ret = ((StringTag)tag).data;
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


	public static AbstractTag from(Object object) {
		if(object instanceof Boolean) {
			return ByteTag.of((Boolean)object);
		} else if(object instanceof Byte) {
			return ByteTag.of((Byte) object);
		} else if(object instanceof Character) {
			return ShortTag.of((short) ((Character)object).charValue());
		} else if(object instanceof Short) {
			return ShortTag.of((Short) object);
		} else if(object instanceof Float) {
			return FloatTag.of((Float) object);
		} else if(object instanceof Integer) {
			return IntTag.of((Integer) object);
		} else if(object instanceof Double) {
			return DoubleTag.of((Double) object);
		} else if(object instanceof Long) {
			return LongTag.of((Long) object);
		} else if(object instanceof NBTagView) {
			return ((NBTagView) object).copyTag();
		} else if(object instanceof IntList) {
			return new IntArrayTag(((IntList) object).toIntArray());
		} else if(object instanceof ByteList) {
			return new ByteArrayTag(((ByteList) object).toByteArray());
		} else if(object instanceof LongList) {
			return new LongArrayTag(((LongList) object).toLongArray());
		} else if(object instanceof List) {
			List<?> objects = (List<?>) object;
			ListTag tag = new ListTag();
			for (Object o : objects) {
				tag.add(from(o));
			}
			return tag;
		} else if(object instanceof String) {
			return StringTag.of((String) object);
		}
		throw new UnsupportedOperationException(object + "");
	}

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

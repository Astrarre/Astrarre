package io.github.astrarre.itemview.v0.api.nbt;

import java.util.Iterator;
import java.util.List;

import io.github.astrarre.itemview.internal.util.ImmutableIterable;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.NbtElement;

@SuppressWarnings({
		"unchecked",
		"rawtypes"
})
@ApiStatus.NonExtendable
public interface NbtValue extends Comparable<NbtValue> {
	/**
	 * @return a new tag value
	 */
	static <T> NbtValue of(NBTType<T> type, T value) {
		return (NbtValue) FabricViews.from(value);
	}
	
	/**
	 * @return the value from the entry as the given value
	 */
	<T> T get(NBTType<T> type);

	default Object asAny() {return this.get(NBTType.ANY);}
	default List<Object> asList() {return this.get(NBTType.ANY_LIST);}
	default Boolean asBool() {return this.get(NBTType.BOOL);}
	default Byte asByte() {return this.get(NBTType.BYTE);}
	default Short asShort() {return this.get(NBTType.SHORT);}
	default Character asChar() {return this.get(NBTType.CHAR);}
	default Integer asInt() {return this.get(NBTType.INT);}
	default Long asLong() {return this.get(NBTType.LONG);}
	default Float asFloat() {return this.get(NBTType.FLOAT);}
	default Double asDouble() {return this.get(NBTType.DOUBLE);}
	default ByteList asByteList() {return this.get(NBTType.BYTE_ARRAY);}
	default String asString() {return this.get(NBTType.STRING);}
	default NBTagView asTag() {return this.get(NBTType.TAG);}
	default IntList asIntList() {return this.get(NBTType.INT_ARRAY);}
	default LongList asLongList() {return this.get(NBTType.LONG_ARRAY);}
	default Number asNumber() {return this.get(NBTType.NUMBER);}

	/**
	 * @deprecated unsafe
	 */
	@Deprecated
	default NbtElement asMinecraft() {
		return (NbtElement) this;
	}

	@Override
	default int compareTo(@NotNull NbtValue o) {
		int compare = Integer.compare(this.asMinecraft().getType(), o.asMinecraft().getType());
		if(compare == 0) {
			Object a = this.asAny(), b = o.asAny();
			if(a instanceof Comparable ca && b instanceof Comparable<?> cb) {
				return ca.compareTo(cb);
			} else if(a instanceof Iterable<?> ia && b instanceof Iterable<?> ib) {
				return ImmutableIterable.compare(ia.iterator(), (Iterator) ib.iterator(), null);
			}
		}
		return compare;
	}
}

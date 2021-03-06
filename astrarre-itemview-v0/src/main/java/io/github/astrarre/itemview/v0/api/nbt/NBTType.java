package io.github.astrarre.itemview.v0.api.nbt;

import java.util.List;
import net.minecraft.nbt.NbtElement;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings ({
		"unchecked",
		"rawtypes"
})
public final class NBTType<T> implements Serializer<T> {
	private static final Int2ObjectMap<NBTType<?>> BY_INTERNAL_ID = new Int2ObjectOpenHashMap<>();
	public static final NBTType<Object> ANY = new NBTType<>(Object.class, 0, -1);
	public static final NBTType<List> ANY_LIST = (NBTType) NBTType.listOf(NBTType.ANY);

	// start
	public static final NBTType<Boolean> BOOL = new NBTType<>(boolean.class, -3, 1);
	public static final NBTType<Byte> BYTE = new NBTType<>(byte.class, 1);
	public static final NBTType<Short> SHORT = new NBTType<>(short.class, 2);
	public static final NBTType<Character> CHAR = new NBTType<>(char.class, -2, 2);
	public static final NBTType<Integer> INT = new NBTType<>(int.class, 3);
	public static final NBTType<Long> LONG = new NBTType<>(long.class, 4);
	public static final NBTType<Float> FLOAT = new NBTType<>(float.class, 5);
	public static final NBTType<Double> DOUBLE = new NBTType<>(double.class, 6);
	public static final NBTType<ByteList> BYTE_ARRAY = new NBTType<>(ByteList.class, 7);
	public static final NBTType<String> STRING = new NBTType<>(String.class, 8);
	// list
	public static final NBTType<NBTagView> TAG = new NBTType<>(NBTagView.class, 10);
	public static final NBTType<IntList> INT_ARRAY = new NBTType<>(IntList.class, 11);
	public static final NBTType<LongList> LONG_ARRAY = new NBTType<>(LongList.class, 12);
	// gap
	public static final NBTType<Number> NUMBER = new NBTType<>(Number.class, 99);

	private final Class<T> cls;
	private final int type, internalType;
	@Nullable private final NBTType<?> component;

	private NBTType(Class<T> cls, int type) {
		this(cls, null, type, type);
	}

	private NBTType(Class<T> cls, @Nullable NBTType<?> component, int type, int internalType) {
		this.cls = cls;
		this.type = type;
		this.component = component;
		this.internalType = internalType;
		BY_INTERNAL_ID.put(internalType, this);
	}

	private NBTType(Class<T> cls, int type, int internalType) {
		this(cls, null, type, internalType);
	}

	public static <T> NBTType<List<T>> listOf(NBTType<T> type) {
		return new NBTType(List.class, type, type.type, 9);
	}

	public Class<T> getClassType() {
		return this.cls;
	}

	/**
	 * @return a unique integer for each root component type (lists will return their root type)
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * @deprecated internal
	 */
	@Deprecated
	public int getInternalType() {
		return this.internalType;
	}

	/**
	 * @deprecated internal
	 */
	@Deprecated
	public boolean internalTypeEquals(int type) {
		return this.internalType == -1 || this.internalType == type || type == 0;
	}

	@Nullable
	public NBTType<?> getRootComponent() {
		NBTType<?> component = this.component;
		if (component == null) {
			return null;
		}

		while (true) {
			NBTType type = component.getComponent();
			if (type == null) {
				return component;
			} else {
				component = type;
			}
		}
	}

	/**
	 * @return null if not a list type
	 */
	@Nullable
	public NBTType<?> getComponent() {
		return this.component;
	}

	@Override
	public T read(NbtValue value) {
		return FabricViews.view((NbtElement) value, this);
	}

	@Override
	public NbtValue save(T instance) {
		return (NbtValue) FabricViews.from(instance);
	}

	/**
	 * @deprecated internal
	 */
	@Deprecated
	public static <T> NBTType<T> forInternal(int internalType) {
		return (NBTType<T>) BY_INTERNAL_ID.get(internalType);
	}
}

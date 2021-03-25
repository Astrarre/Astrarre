package io.github.astrarre.itemview.v0.api;

import java.util.function.BiFunction;
import java.util.function.Function;

import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.util.v0.api.Id;
import org.apache.logging.log4j.util.TriConsumer;

/**
 * @see NBTType for primitive serializers
 */
public interface Serializer<T> {
	Serializer<Id> ID = of(t -> Id.create(t.asString()), v -> NbtValue.of(NBTType.STRING, v.toString()));
	Serializer<NBTagView> NBT = of(n -> (NBTagView) n, n -> n);

	static <T> Serializer<T> of(Function<NbtValue, T> reader, Function<T, NbtValue> writer) {
		return new Serializer<T>() {
			@Override
			public T read(NbtValue value) {
				return reader.apply(value);
			}

			@Override
			public NbtValue save(T instance) {
				return writer.apply(instance);
			}
		};
	}

	static <T extends Serializable> Serializer<T> of(Function<NbtValue, T> reader) {
		return of(reader, Serializable::save);
	}

	static <T extends Enum<T>> Serializer<T> ofEnum(Class<T> type) {
		T[] elements = type.getEnumConstants();
		return of((tag) -> {
			int ordinal = tag.get(NBTType.INT);
			if(ordinal < elements.length) {
				return elements[ordinal];
			} else {
				return elements[0];
			}
		}, t -> NbtValue.of(NBTType.INT, t.ordinal()));
	}

	default T read(NBTagView view, String key) {
		return this.read(view.getValue(key));
	}

	/**
	 * @return a new instance read from the input
	 */
	T read(NbtValue value);
	NbtValue save(T instance);

	default void save(NBTagView.Builder tag, String value, T object) {
		tag.putValue(value, this.save(object));
	}
}

package io.github.astrarre.itemview.v0.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.func.Copier;
import org.apache.logging.log4j.util.TriConsumer;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

/**
 * @see NBTType for primitive serializers
 */
public interface Serializer<T> extends Copier<T> {
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

	/**
	 * @see #ofCollection(Serializer, Supplier)
	 */
	static <T> Serializer<List<T>> ofList(Serializer<T> serializer) {
		return ofCollection(serializer, ArrayList::new);
	}

	/**
	 * creates a serializer for a collection of serializable objects
	 */
	static <T, C extends Collection<T>> Serializer<C> ofCollection(Serializer<T> serializer,
			Supplier<C> newCollection) {
		return of(e -> {
			C collection = newCollection.get();
			for (NbtElement element : ((NbtList)e)) {
				collection.add(serializer.read((NbtValue) element));
			}
			return collection;
		}, ts -> {
			NbtList list = new NbtList();
			for (T t : ts) {
				list.add(serializer.save(t).asMinecraft());
			}
			return (NbtValue) list;
		});
	}

	static <K, V, M extends Map<K, V>> Serializer<M> ofMap(Serializer<K> keySerializer,
			Serializer<V> valueSerializer, Supplier<M> newMap) {
		return FabricSerializers.<NbtCompound, M>of(element -> {
			M map = newMap.get();
			NbtList keys = (NbtList) element.get("keys");
			NbtList values = (NbtList) element.get("values");
			for (int i = 0; i < keys.size(); i++) {
				map.put(keySerializer.read((NbtValue) keys.get(i)), valueSerializer.read((NbtValue) values.get(i)));
			}
			return map;
		}, m -> {
			NbtCompound compound = new NbtCompound();
			NbtList keys = new NbtList();
			NbtList values = new NbtList();
			for (Map.Entry<K, V> entry : m.entrySet()) {
				keys.add(keySerializer.save(entry.getKey()).asMinecraft());
				values.add(valueSerializer.save(entry.getValue()).asMinecraft());
			}
			compound.put("keys", keys);
			compound.put("values", values);
			return compound;
		});
	}

	@Override
	default T copy(T val) {
		return this.read(this.save(val));
	}
}

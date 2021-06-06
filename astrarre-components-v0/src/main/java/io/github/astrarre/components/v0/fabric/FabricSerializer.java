package io.github.astrarre.components.v0.fabric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.astrarre.components.v0.api.Copier;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.NbtTypes;
import net.minecraft.network.PacketByteBuf;

// todo custom I/O module with all of this stuff
// todo maybe just use codecs?
public interface FabricSerializer<T, E extends NbtElement> extends Copier<T>, FabricByteSerializer<T> {
	FabricSerializer<String, ?> STRING = of(NbtString::of, NbtString::asString);

	/**
	 * @see #tagSerializer()
	 */
	FabricSerializer<?, ? extends NbtElement> IDENTITY = of(Function.identity(), Function.identity());

	/**
	 * serialize the object to an NbtElement
	 */
	E toTag(T object);

	/**
	 * serialize the object from an NbtElement
	 */
	T fromTag(E element);

	static <T, E extends NbtElement> FabricSerializer<T, E> of(Function<T, E> toTag, Function<E, T> fromTag) {
		return new FabricSerializer<>() {
			@Override
			public E toTag(T object) {
				return toTag.apply(object);
			}

			@Override
			public T fromTag(E element) {
				return fromTag.apply(element);
			}
		};
	}

	/**
	 * Serializer for Nbt tags. Useful for serializing CompoundTags when api adapting is needed.
	 */
	static <E extends NbtElement> FabricSerializer<E, E> tagSerializer() {
		return (FabricSerializer<E, E>) IDENTITY;
	}

	/**
	 * @see #ofCollection(FabricSerializer, Supplier)
	 */
	static <T> FabricSerializer<List<T>, ?> ofList(FabricSerializer<T, ?> serializer) {
		return ofCollection(serializer, ArrayList::new);
	}

	/**
	 * creates a serializer for a collection of serializable objects
	 */
	static <T, C extends Collection<T>, E extends NbtElement> FabricSerializer<C, ?> ofCollection(FabricSerializer<T, E> serializer,
			Supplier<C> newCollection) {
		return of(ts -> {
			NbtList list = new NbtList();
			for (T t : ts) {
				list.add(serializer.toTag(t));
			}
			return list;
		}, e -> {
			C collection = newCollection.get();
			for (NbtElement element : e) {
				collection.add(serializer.fromTag((E) element));
			}
			return collection;
		});
	}


	static <K, V, M extends Map<K, V>, KE extends NbtElement, VE extends NbtElement> FabricSerializer<M, ?> ofMap(FabricSerializer<K, KE> keySerializer,
			FabricSerializer<V, VE> valueSerializer, Supplier<M> newMap) {
		return FabricSerializer.of(m -> {
			NbtCompound compound = new NbtCompound();
			NbtList keys = new NbtList();
			NbtList values = new NbtList();
			for (Map.Entry<K, V> entry : m.entrySet()) {
				keys.add(keySerializer.toTag(entry.getKey()));
				values.add(valueSerializer.toTag(entry.getValue()));
			}
			compound.put("keys", keys);
			compound.put("values", values);
			return compound;
		}, element -> {
			M map = newMap.get();
			NbtList keys = (NbtList) element.get("keys");
			NbtList values = (NbtList) element.get("values");
			for (int i = 0; i < keys.size(); i++) {
				map.put(keySerializer.fromTag((KE) keys.get(i)), valueSerializer.fromTag((VE) values.get(i)));
			}
			return map;
		});
	}

	@Override
	default T copy(T val) {
		return this.fromTag(this.toTag(val));
	}

	@Override
	default T fromBytes(PacketByteBuf buf) throws IOException {
		NbtType<?> type = NbtTypes.byId(buf.readInt());
		return this.fromTag((E) type.read(new ByteBufInputStream(buf), 1, NbtTagSizeTracker.EMPTY));
	}

	@Override
	default void toBytes(T val, PacketByteBuf buf) throws IOException {
		E element = toTag(val);
		buf.writeInt(element.getType());
		element.write(new ByteBufOutputStream(buf));
	}
}

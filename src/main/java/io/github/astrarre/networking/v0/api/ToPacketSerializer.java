package io.github.astrarre.networking.v0.api;

import java.util.function.BiConsumer;
import java.util.function.Function;

import io.github.astrarre.v0.network.PacketByteBuf;
import io.github.astrarre.v0.util.Id;

/**
 * an interface used for serializing and deserializing objects
 * @param <T>
 */
public interface ToPacketSerializer<T> {
	ToPacketSerializer<String> STRING = of(PacketByteBuf::writeString, PacketByteBuf::readString);
	ToPacketSerializer<Integer> INTEGER = of(PacketByteBuf::writeInt, PacketByteBuf::readInt);
	ToPacketSerializer<Id> IDENTIFIER = of((p, i) -> p.writeString(i.toString()), buf -> Id.newInstance(buf.readString()));

	void write(PacketByteBuf out, T obj);
	T read(PacketByteBuf in);

	static <T> ToPacketSerializer<T> of(BiConsumer<PacketByteBuf, T> write, Function<PacketByteBuf, T> read) {
		return new ToPacketSerializer<T>() {
			@Override
			public void write(PacketByteBuf out, T obj) {
				write.accept(out, obj);
			}

			@Override
			public T read(PacketByteBuf in) {
				return read.apply(in);
			}
		};
	}
}

package io.github.astrarre.v0.api.network;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.v0.network.PacketByteBuf;
import io.netty.buffer.Unpooled;

/**
 * a wrapper for a property that can be synced to the client or server
 * @param <T>
 */
public abstract class SharedProperty<T> {
	protected final BiConsumer<PacketByteBuf, T> serializer;
	protected final Function<PacketByteBuf, T> deserializer;
	protected final Consumer<T> onSet;
	private T value;

	public SharedProperty(BiConsumer<PacketByteBuf, T> serializer,
			Function<PacketByteBuf, T> deserializer,
			Consumer<T> onSet) {
		this.serializer = serializer;
		this.deserializer = deserializer;
		this.onSet = onSet;
	}

	/**
	 * send the packet to it's destination
	 */
	protected abstract void send(PacketByteBuf buf);

	public T get() {
		return this.value;
	}

	/**
	 * @return the old value
	 */
	public T set(T value) {
		T old = this.value;
		this.value = value;

		this.onSet.accept(value);
		PacketByteBuf buf = PacketByteBuf.newInstance(Unpooled.buffer());
		this.serializer.accept(buf, value);
		this.send(buf);

		return old;
	}

	/**
	 * forcefully updates the property
	 */
	public void resync() {
		this.set(this.get());
	}
}

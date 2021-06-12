package io.github.astrarre.components.v0.fabric;

import java.io.IOException;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.util.v0.api.func.Copier;
import io.netty.buffer.Unpooled;

import net.minecraft.network.PacketByteBuf;

public interface FabricByteSerializer<T> extends Copier<T> {

	T fromBytes(PacketByteBuf buf) throws IOException;

	void toBytes(T val, PacketByteBuf buf) throws IOException;

	@Override
	default T copy(T val) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		try {
			this.toBytes(val, buf);
			buf.readerIndex(0);
			return this.fromBytes(buf);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

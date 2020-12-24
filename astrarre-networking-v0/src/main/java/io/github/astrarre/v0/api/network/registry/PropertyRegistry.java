package io.github.astrarre.v0.api.network.registry;

import java.io.Closeable;
import java.io.IOException;

import io.github.astrarre.v0.api.network.SyncedProperty;
import io.github.astrarre.v0.api.network.serialization.ToPacketSerializer;
import io.github.astrarre.v0.network.PacketByteBuf;
import io.github.astrarre.v0.util.Id;
import org.jetbrains.annotations.NotNull;

/**
 * a property registry is meant to be a singleton instance that manages incoming and outgoing packets. You are not meant to create and throw away
 * these. However, if you do need to do it, make sure you close the registry when you are done with it
 *
 * @see PropertyRegistry#close()
 */
public abstract class PropertyRegistry<T> implements ModPacketHandler.Receiver, Closeable {
	private final Id id;
	private final ToPacketSerializer<T> serializer;

	protected PropertyRegistry(Id id, ToPacketSerializer<T> serializer) {
		this.id = id;
		this.serializer = serializer;
	}

	@Override
	public void accept(Id id, PacketByteBuf buf) {
		if (this.id.equals(id)) {
			SyncedProperty<T> property = this.find(buf);
		}
	}

	/**
	 * Find the property for the given data. eg. a Block Synced Property would read the x, y, z, world, and id for it then it would find that
	 * block in
	 * the world, and return the property for that block.
	 *
	 * @param buf the full packet data
	 * @return the property found at the location
	 */
	@NotNull
	protected abstract SyncedProperty<T> find(PacketByteBuf buf);

	public static class Server<T> extends PropertyRegistry<T> {
		protected Server(Id id, ToPacketSerializer<T> serializer) {
			super(id, serializer);
		}

		@Override
		protected @NotNull SyncedProperty<T> find(PacketByteBuf buf) {
			return null;
		}

		@Override
		public void close() throws IOException {

		}
	}
}

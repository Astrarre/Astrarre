package io.github.astrarre.components.v0.fabric.builder;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.components.PrimitiveComponent;
import io.github.astrarre.components.v0.api.factory.ComponentManager;
import io.github.astrarre.components.v0.fabric.FabricByteSerializer;
import io.github.astrarre.components.v0.fabric.FabricComponents;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.util.v0.api.func.Copier;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class SerializableComponentBuilder<C, V, T extends Component<C, V>> extends SynchronizableComponentBuilder<C, V, T> {
	protected final Map<String, Pair<Component<C, ?>, Serializer<?>>> serializeInternal;
	protected boolean serialize, set;
	protected Copier<V> copier;

	protected SerializableComponentBuilder(ComponentManager<C> manager,
			Map<String, Pair<Component<C, ?>, FabricByteSerializer<?>>> synchronize,
			Map<String, Pair<Component<C, ?>, Serializer<?>>> internal) {
		super(manager, synchronize);
		this.serializeInternal = internal;
	}

	/**
	 * @see #serialize(Serializer)
	 */
	public SerializableComponentBuilder<C, V, T> serializePrimitive() {
		return this.serialize(null);
	}

	/**
	 * States the entity component is serialized and deserialized wit the entity. This also means it is copied when the entity changes dimensions or
	 * is copied in some other way.
	 */
	public SerializableComponentBuilder<C, V, T> serialize(Serializer<V> serializer) {
		if(this.set) {
			throw new IllegalArgumentException("cannot set similar property twice!");
		}
		this.set = true;
		this.serialize = true;
		this.copier = serializer;

		return this;
	}

	@Nullable
	public static <V, C> CustomPayloadS2CPacket syncWorldBased(Identifier packetId,
			FabricByteSerializer<V> serializer,
			Component<C, V> component,
			C context,
			boolean send,
			Function<C, World> function,
			BiConsumer<C, Packet<?>> sender,
			BiConsumer<C, PacketByteBuf> contextSerializer) {

		World world = function.apply(context);
		if(world instanceof ServerWorld w && !w.isClient) {
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeIdentifier(world.getRegistryKey().getValue());
			contextSerializer.accept(context, buf);
			buf.writeString(component.getMod() + ":" + component.getId());
			try {
				FabricComponents.serialize(buf, context, component, serializer);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}

			var packet = new CustomPayloadS2CPacket(packetId, buf);
			if(send) {
				sender.accept(context, packet);
			}
			return packet;
		}
		return null;
	}

	@Override
	protected void validate(Class<T> type, boolean isPrimitive) {
		super.validate(type, isPrimitive);
		if(!isPrimitive && this.copier == null) {
			throw new IllegalArgumentException("Serializer cannot be null for non-primitive components!");
		}
	}

	/**
	 * @see #serialize(Serializer)
	 */
	@Override
	public T build(Identifier id, Class<T> type) {
		this.validate(type, PrimitiveComponent.class.isAssignableFrom(type));
		T component = this.manager.create(type, id.getNamespace(), id.getPath());
		if(this.serialize) {
			this.serializeInternal.put(component.getMod() + ":" + component.getId(), new Pair(component, this.copier));
		}
		if(this.synchronizing) {
			this.synchronize.put(component.getMod() + ":" + component.getId(), new Pair(component, this.serializer));
			this.setSynchronizing(component);
		}
		return component;
	}

	@Override
	public SerializableComponentBuilder<C, V, T> syncPrimitive() {
		return (SerializableComponentBuilder<C, V, T>) super.syncPrimitive();
	}

	@Override
	public SerializableComponentBuilder<C, V, T> sync(FabricByteSerializer<V> serializer) {
		return (SerializableComponentBuilder<C, V, T>) super.sync(serializer);
	}
}

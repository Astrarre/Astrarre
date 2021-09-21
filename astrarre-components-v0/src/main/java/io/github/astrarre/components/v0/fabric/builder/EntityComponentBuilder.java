package io.github.astrarre.components.v0.fabric.builder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.components.PrimitiveComponent;
import io.github.astrarre.components.v0.api.factory.ComponentManager;
import io.github.astrarre.components.v0.fabric.FabricByteSerializer;
import io.github.astrarre.components.v0.fabric.FabricComponents;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.util.v0.api.func.Copier;
import io.netty.buffer.Unpooled;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

// todo synchronization
public class EntityComponentBuilder<C extends Entity, V, T extends Component<C, V>> extends SerializableComponentBuilder<C, V, T> {
	protected final List<Pair<Component<C, ?>, Copier<?>>> copyInternal;
	protected boolean copy;

	protected EntityComponentBuilder(ComponentManager<C> manager,
			Map<String, Pair<Component<C, ?>, FabricByteSerializer<?>>> synchronize,
			Map<String, Pair<Component<C, ?>, Serializer<?>>> internal,
			List<Pair<Component<C, ?>, Copier<?>>> copyInternal) {
		super(manager, synchronize, internal);
		this.copyInternal = copyInternal;
	}

	public static <V, T extends Component<Entity, V>> EntityComponentBuilder<Entity, V, T> entity() {
		return new EntityComponentBuilder<>(
				ComponentsInternal.ENTITY_MANAGER,
				ComponentsInternal.SYNC_ENTITY_INTERNAL,
				ComponentsInternal.SERIALIZE_ENTITY_INTERNAL,
				ComponentsInternal.COPY_ENTITY_INTENRAL);
	}

	/**
	 * @see #copy(Copier)
	 * @see #serialize(Serializer)
	 */
	@Override
	public T build(Identifier id, Class<T> type) {
		this.validate(type, PrimitiveComponent.class.isAssignableFrom(type));
		if (this.serialize) {
			return super.build(id, type);
		} else {
			T component = this.manager.create(type, id.getNamespace(), id.getPath());
			if (this.copy) {
				this.copyInternal.add(new Pair<>(component, this.copier));
			}
			if (this.synchronizing) {
				this.synchronize.put(component.getMod() + ":" + component.getId(), new Pair<>(component, this.serializer));
				this.setSynchronizing(component);
			}
			return component;
		}
	}

	@Override
	public EntityComponentBuilder<C, V, T> serializePrimitive() {
		return (EntityComponentBuilder<C, V, T>) super.serializePrimitive();
	}

	@Override
	public EntityComponentBuilder<C, V, T> serialize(Serializer<V> serializer) {
		return (EntityComponentBuilder<C, V, T>) super.serialize(serializer);
	}

	@Override
	public EntityComponentBuilder<C, V, T> syncPrimitive() {
		return (EntityComponentBuilder<C, V, T>) super.syncPrimitive();
	}

	@Override
	public EntityComponentBuilder<C, V, T> sync(FabricByteSerializer<V> serializer) {
		return (EntityComponentBuilder<C, V, T>) super.sync(serializer);
	}

	@Override
	protected void setSynchronizing(T component) {
		component.postChange((c, v) -> sync(ComponentsInternal.SYNC_ENTITY, this.serializer, component, c, true));
	}

	public static <V, C extends Entity> Packet<?> sync(Identifier packetId, FabricByteSerializer<V> serializer, Component<C, V> component, C context, boolean send) {
		return SerializableComponentBuilder.syncWorldBased(packetId, serializer, component, context, send, Entity::getEntityWorld, (c, p) -> {
			ServerChunkManager manager = ((ServerChunkManager) c.getEntityWorld().getChunkManager());
			manager.sendToNearbyPlayers(c, p);
		}, (c, b) -> b.writeInt(c.getId()));
	}

	/**
	 * @see #copy(Copier)
	 */
	public EntityComponentBuilder<C, V, T> copyPrimitive() {
		return this.copy(null);
	}

	/**
	 * States the component is copied with the entity (eg. non-player dimension change)
	 */
	public EntityComponentBuilder<C, V, T> copy(Copier<V> copier) {
		if (this.set) {
			throw new IllegalArgumentException("cannot set similar property twice!");
		}
		this.set = true;
		this.copy = true;
		this.copier = copier;

		return this;
	}

	@Override
	protected void validate(Class<T> type, boolean isPrimitive) {
		super.validate(type, isPrimitive);
	}
}
package io.github.astrarre.components.v0.fabric;

import java.util.List;
import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.v0.api.Copier;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.components.PrimitiveComponent;
import io.github.astrarre.components.v0.api.factory.ComponentManager;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

// todo synchronization
public class EntityComponentBuilder<C extends Entity, V, T extends Component<C, V>> {
	public static <V, T extends Component<Entity, V>> EntityComponentBuilder<Entity, V, T> entity(Class<T> type) {
		return new EntityComponentBuilder<>(ComponentsInternal.MANAGER, ComponentsInternal.COPY_ENTITY_INTENRAL, ComponentsInternal.SERIALIZE_ENTITY_INTERNAL, type);
	}

	protected final boolean isPrimitive;
	protected final ComponentManager<C> manager;
	protected final List<Pair<Component<Entity, ?>, Copier<?>>> copyInternal;
	protected final Map<String, Pair<Component<Entity, ?>, FabricSerializer<?, ?>>> serializeInternal;

	protected final Class<T> componentType;
	protected boolean copy, serialize, set;
	protected Copier<V> copier;
	protected EntityComponentBuilder(ComponentManager<C> manager,
			List<Pair<Component<Entity, ?>, Copier<?>>> internal,
			Map<String, Pair<Component<Entity, ?>, FabricSerializer<?, ?>>> serializeInternal,
			Class<T> componentType) {
		this.manager = manager;
		this.copyInternal = internal;
		this.serializeInternal = serializeInternal;
		this.componentType = componentType;
		this.isPrimitive = PrimitiveComponent.class.isAssignableFrom(componentType);
	}

	/**
	 * Not copied when the entity moves dimensions or cloned. Not serialized/deserialized from NBT.
	 *
	 * @see #copy(Copier)
	 * @see #serialize(FabricSerializer)
	 */
	public T build(Identifier id) {
		T component = this.manager.create(this.componentType, id.getNamespace(), id.getPath());
		if (this.copy) {
			this.copyInternal.add(new Pair(component, this.copier));
		} else if (this.serialize) {
			this.serializeInternal.put(component.getMod() + ":" + component.getId(), new Pair(component, this.copier));
		}
		return component;
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
		if (!this.isPrimitive && copier == null) {
			throw new IllegalArgumentException("Copier cannot be null for non-primitive components!");
		}
		if (this.set) {
			throw new IllegalArgumentException("cannot set similar property twice!");
		}
		this.set = true;
		this.copy = true;
		this.copier = copier;

		return this;
	}

	/**
	 * @see #serialize(FabricSerializer)
	 */
	public EntityComponentBuilder<C, V, T> serializePrimitive() {
		return this.serialize(null);
	}

	/**
	 * States the entity component is serialized and deserialized wit the entity. This also means it is copied when the entity changes dimensions or
	 * is copied in some other way.
	 */
	public EntityComponentBuilder<C, V, T> serialize(FabricSerializer<V, ?> serializer) {
		if (!this.isPrimitive && serializer == null) {
			throw new IllegalArgumentException("Serializer cannot be null for non-primitive components!");
		}

		if (this.set) {
			throw new IllegalArgumentException("cannot set similar property twice!");
		}
		this.set = true;
		this.serialize = true;
		this.copier = serializer;

		return this;
	}
}
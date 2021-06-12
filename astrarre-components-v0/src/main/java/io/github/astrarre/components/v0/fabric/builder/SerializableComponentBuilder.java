package io.github.astrarre.components.v0.fabric.builder;

import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.components.PrimitiveComponent;
import io.github.astrarre.components.v0.api.factory.ComponentManager;
import io.github.astrarre.components.v0.fabric.FabricByteSerializer;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.util.v0.api.func.Copier;

import net.minecraft.util.Identifier;

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

	@Override
	protected void validate(Class<T> type, boolean isPrimitive) {
		super.validate(type, isPrimitive);
		if (!isPrimitive && this.copier == null) {
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
		if (this.serialize) {
			this.serializeInternal.put(component.getMod() + ":" + component.getId(), new Pair(component, this.copier));
		}
		if (this.synchronizing) {
			this.synchronize.put(component.getMod() + ":" + component.getId(), new Pair(component, this.serializer));
			this.setSynchronizing(component);
		}
		return component;
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
		if (this.set) {
			throw new IllegalArgumentException("cannot set similar property twice!");
		}
		this.set = true;
		this.serialize = true;
		this.copier = serializer;

		return this;
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

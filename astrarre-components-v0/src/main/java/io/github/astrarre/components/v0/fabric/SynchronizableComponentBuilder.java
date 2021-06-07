package io.github.astrarre.components.v0.fabric;

import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.v0.api.components.BoolComponent;
import io.github.astrarre.components.v0.api.components.ByteComponent;
import io.github.astrarre.components.v0.api.components.CharComponent;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.components.DoubleComponent;
import io.github.astrarre.components.v0.api.components.FloatComponent;
import io.github.astrarre.components.v0.api.components.IntComponent;
import io.github.astrarre.components.v0.api.components.LongComponent;
import io.github.astrarre.components.v0.api.components.PrimitiveComponent;
import io.github.astrarre.components.v0.api.components.ShortComponent;
import io.github.astrarre.components.v0.api.factory.ComponentManager;

import net.minecraft.util.Identifier;

public abstract class SynchronizableComponentBuilder<C, V, T extends Component<C, V>> {
	protected final Map<String, Pair<Component<C, ?>, FabricByteSerializer<?>>> synchronize;
	protected final ComponentManager<C> manager;
	protected boolean synchronizing;
	protected FabricByteSerializer<V> serializer;

	protected SynchronizableComponentBuilder(ComponentManager<C> manager,
			Map<String, Pair<Component<C, ?>, FabricByteSerializer<?>>> synchronize) {
		this.synchronize = synchronize;
		this.manager = manager;
	}

	protected void validate(Class<T> type, boolean isPrimitive) {
		if (!isPrimitive && this.serializer == null) {
			throw new IllegalArgumentException("Serializer cannot be null for non-primitive components!");
		}
	}

	public BoolComponent<C> buildBool(Identifier id) {
		return (BoolComponent<C>) this.build(id, (Class<T>) BoolComponent.class);
	}

	public ByteComponent<C> buildByte(Identifier id) {
		return (ByteComponent<C>) this.build(id, (Class<T>) ByteComponent.class);
	}

	public ShortComponent<C> buildShort(Identifier id) {
		return (ShortComponent<C>) this.build(id, (Class<T>) ShortComponent.class);
	}

	public CharComponent<C> buildChar(Identifier id) {
		return (CharComponent<C>) this.build(id, (Class<T>) CharComponent.class);
	}

	public IntComponent<C> buildInt(Identifier id) {
		return (IntComponent<C>) this.build(id, (Class<T>) IntComponent.class);
	}

	public FloatComponent<C> buildFloat(Identifier id) {
		return (FloatComponent<C>) this.build(id, (Class<T>) FloatComponent.class);
	}

	public LongComponent<C> buildLong(Identifier id) {
		return (LongComponent<C>) this.build(id, (Class<T>) LongComponent.class);
	}

	public DoubleComponent<C> buildDouble(Identifier id) {
		return (DoubleComponent<C>) this.build(id, (Class<T>) DoubleComponent.class);
	}

	public T buildObj(Identifier id) {
		return this.build(id, (Class<T>) Component.class);
	}

	/**
	 * @see #sync(FabricByteSerializer)
	 */
	public T build(Identifier id, Class<T> type) {
		this.validate(type, PrimitiveComponent.class.isAssignableFrom(type));
		T component = this.manager.create(type, id.getNamespace(), id.getPath());
		if (this.synchronizing) {
			this.synchronize.put(component.getMod() + ":" + component.getId(), new Pair(component, this.serializer));
			this.setSynchronizing(component);
		}
		return component;
	}

	protected abstract void setSynchronizing(T component);

	/**
	 * @see #sync(FabricByteSerializer)
	 */
	public SynchronizableComponentBuilder<C, V, T> syncPrimitive() {
		return this.sync(null);
	}

	/**
	 * States the entity component is serialized and deserialized wit the entity. This also means it is copied when the entity changes dimensions or
	 * is copied in some other way.
	 */
	public SynchronizableComponentBuilder<C, V, T> sync(FabricByteSerializer<V> serializer) {


		if (this.synchronizing) {
			throw new IllegalArgumentException("cannot set similar property twice!");
		}
		this.synchronizing = true;
		this.serializer = serializer;
		return this;
	}
}

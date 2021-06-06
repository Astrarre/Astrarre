package io.github.astrarre.components.v0.fabric;

import java.util.List;
import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.v0.api.Copier;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.factory.ComponentManager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerComponentBuilder<V, T extends Component<PlayerEntity, V>> extends EntityComponentBuilder<PlayerEntity, V, T> {
	// copies on death only if keep inventory is on
	protected final List<Pair<Component<PlayerEntity, ?>, Copier<?>>> copyPlayerInventory;

	// always copies on death or move dimension
	protected final List<Pair<Component<PlayerEntity, ?>, Copier<?>>> copyPlayerAlways;

	protected boolean copyInventory, copyAlways;

	protected PlayerComponentBuilder(ComponentManager<PlayerEntity> manager,
			List<Pair<Component<Entity, ?>, Copier<?>>> internal,
			Map<String, Pair<Component<Entity, ?>, FabricSerializer<?, ?>>> serializeInternal,
			Class<T> componentType,
			List<Pair<Component<PlayerEntity, ?>, Copier<?>>> copyPlayerInventory,
			List<Pair<Component<PlayerEntity, ?>, Copier<?>>> copyPlayerAlways) {
		super(manager, internal, serializeInternal, componentType);
		this.copyPlayerInventory = copyPlayerInventory;
		this.copyPlayerAlways = copyPlayerAlways;
	}

	@Override
	public T build(Identifier id) {
		if (this.copyInventory) {
			T component = this.manager.create(this.componentType, id.getNamespace(), id.getPath());
			this.copyPlayerInventory.add(new Pair(component, this.copier));
			return component;
		} else if (this.copyAlways) {
			T component = this.manager.create(this.componentType, id.getNamespace(), id.getPath());
			this.copyPlayerAlways.add(new Pair(component, this.copier));
			return component;
		} else {
			return super.build(id);
		}
	}

	@Override
	public PlayerComponentBuilder<V, T> copyPrimitive() {
		return (PlayerComponentBuilder<V, T>) super.copyPrimitive();
	}

	@Override
	public PlayerComponentBuilder<V, T> copy(Copier<V> copier) {
		return (PlayerComponentBuilder<V, T>) super.copy(copier);
	}

	@Override
	public PlayerComponentBuilder<V, T> serializePrimitive() {
		return (PlayerComponentBuilder<V, T>) super.serializePrimitive();
	}

	@Override
	public PlayerComponentBuilder<V, T> serialize(FabricSerializer<V, ?> serializer) {
		return (PlayerComponentBuilder<V, T>) super.serialize(serializer);
	}

	/**
	 * @see #copyInventory(Copier)
	 */
	public PlayerComponentBuilder<V, T> copyInventoryPrimitive() {
		return this.copy(null);
	}

	/**
	 * This is copied only if the player dies and their inventory is preserved. (eg. gamerule keepInventory = true)
	 */
	public PlayerComponentBuilder<V, T> copyInventory(Copier<V> copier) {
		if (!this.isPrimitive && copier == null) {
			throw new IllegalArgumentException("Copier cannot be null for non-primitive components!");
		}
		if (this.set) {
			throw new IllegalArgumentException("cannot set similar property twice!");
		}
		this.set = true;
		this.copyInventory = true;
		this.copier = copier;

		return this;
	}

	/**
	 * @see #copyAlways(Copier)
	 */
	public PlayerComponentBuilder<V, T> copyAlwaysPrimitive() {
		return this.copy(null);
	}

	/**
	 * This is copied only if the player dies and their always is preserved. (eg. gamerule keepAlways = true).
	 * This is only done for ServerPlayerEntities
	 */
	public PlayerComponentBuilder<V, T> copyAlways(Copier<V> copier) {
		if (!this.isPrimitive && copier == null) {
			throw new IllegalArgumentException("Copier cannot be null for non-primitive components!");
		}
		if (this.set) {
			throw new IllegalArgumentException("cannot set similar property twice!");
		}
		this.set = true;
		this.copyAlways = true;
		this.copier = copier;

		return this;
	}
}

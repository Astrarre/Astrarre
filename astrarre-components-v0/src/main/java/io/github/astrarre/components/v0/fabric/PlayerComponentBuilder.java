package io.github.astrarre.components.v0.fabric;

import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.v0.api.Copier;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.factory.ComponentManager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerComponentBuilder<V, T extends Component<PlayerEntity, V>> extends EntityComponentBuilder<PlayerEntity, V, T> {
	public static <V, T extends Component<PlayerEntity, V>> PlayerComponentBuilder<V, T> player() {
		return new PlayerComponentBuilder<>(
				ComponentsInternal.PLAYER_MANAGER,
				ComponentsInternal.SYNC_PLAYER_INTERNAL,
				ComponentsInternal.SERIALIZE_PLAYER_INTERNAL,
				ComponentsInternal.COPY_PLAYER_ALIVE,
				ComponentsInternal.COPY_PLAYER_INVENTORY,
				ComponentsInternal.COPY_PLAYER_ALWAYS);
	}

	// copies on death only if keep inventory is on
	protected final List<Pair<Component<PlayerEntity, ?>, Copier<?>>> copyPlayerInventory;

	// always copies on death or move dimension
	protected final List<Pair<Component<PlayerEntity, ?>, Copier<?>>> copyPlayerAlways;

	protected boolean copyInventory, copyAlways;

	protected PlayerComponentBuilder(ComponentManager<PlayerEntity> manager,
			Map<String, Pair<Component<PlayerEntity, ?>, FabricByteSerializer<?>>> synchronize,
			Map<String, Pair<Component<PlayerEntity, ?>, FabricSerializer<?, ?>>> internal,
			List<Pair<Component<PlayerEntity, ?>, Copier<?>>> copyInternal,
			List<Pair<Component<PlayerEntity, ?>, Copier<?>>> copyPlayerInventory,
			List<Pair<Component<PlayerEntity, ?>, Copier<?>>> copyPlayerAlways) {
		super(manager, synchronize, internal, copyInternal);
		this.copyPlayerInventory = copyPlayerInventory;
		this.copyPlayerAlways = copyPlayerAlways;
	}


	@Override
	public T build(Identifier id, Class<T> type) {
		if (this.copyInventory) {
			T component = this.manager.create(type, id.getNamespace(), id.getPath());
			this.copyPlayerInventory.add(new Pair(component, this.copier));
			if (this.synchronizing) {
				this.synchronize.put(component.getMod() + ":" + component.getId(), new Pair(component, this.serializer));
				this.setSynchronizing(component);
			}
			return component;
		} else if (this.copyAlways) {
			T component = this.manager.create(type, id.getNamespace(), id.getPath());
			this.copyPlayerAlways.add(new Pair(component, this.copier));
			if (this.synchronizing) {
				this.synchronize.put(component.getMod() + ":" + component.getId(), new Pair(component, this.serializer));
				this.setSynchronizing(component);
			}
			return component;
		} else {
			return super.build(id, type);
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
		PlayerComponentBuilder<V, T> builder = (PlayerComponentBuilder<V, T>) super.serializePrimitive();
		this.set = false;
		return builder;
	}

	@Override
	public PlayerComponentBuilder<V, T> serialize(FabricSerializer<V, ?> serializer) {
		PlayerComponentBuilder<V, T> serialize = (PlayerComponentBuilder<V, T>) super.serialize(serializer);
		this.set = false;
		return serialize;
	}

	@Override
	public PlayerComponentBuilder<V, T> syncPrimitive() {
		return (PlayerComponentBuilder<V, T>) super.syncPrimitive();
	}

	@Override
	public PlayerComponentBuilder<V, T> sync(FabricByteSerializer<V> serializer) {
		return (PlayerComponentBuilder<V, T>) super.sync(serializer);
	}

	/**
	 * @see #copyInventory(Copier)
	 */
	public PlayerComponentBuilder<V, T> copyInventoryPrimitive() {
		return this.copyInventory(null);
	}

	/**
	 * This is copied only if the player dies and their inventory is preserved. (eg. gamerule keepInventory = true)
	 */
	public PlayerComponentBuilder<V, T> copyInventory(Copier<V> copier) {
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
		if (this.set) {
			throw new IllegalArgumentException("cannot set similar property twice!");
		}
		this.set = true;
		this.copyAlways = true;
		this.copier = copier;

		return this;
	}

	@Override
	protected void setSynchronizing(T component) {
		component.postChange((c, v) -> sync(ComponentsInternal.SYNC_PLAYER, this.serializer, component, c, true));
	}
}

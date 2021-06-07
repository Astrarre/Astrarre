package io.github.astrarre.components.v0.api.factory;

import io.github.astrarre.components.internal.lazyAsm.DataObjectHolderComponentManager;
import io.github.astrarre.components.internal.lazyAsm.PlayerDataObjectHolderComponentManager;
import io.github.astrarre.components.v0.api.components.Component;

import net.minecraft.entity.player.PlayerEntity;

public interface ComponentManager<C> {
	/**
	 * @param <C> the context class, must implement {@link DataObjectHolder}.
	 * If another mod implements it on the same class or super class, this may conflict and cause issues.
	 * @see #newPlayerManager(String, String)
	 */
	static <C> ComponentManager<C> newManager(String modid, String path) {
		return new DataObjectHolderComponentManager<>(modid, path);
	}

	/**
	 * There is nothing special about this manager, except that it uses an internal version of DataObjectHolder.
	 * PlayerEntity already implements {@link DataObjectHolder} because it extends Entity.
	 * PlayerEntity-specific behavior requires a seperate data object holder, which this uses
	 */
	static ComponentManager<PlayerEntity> newPlayerManager(String modid, String path) {
		return new PlayerDataObjectHolderComponentManager(modid, path);
	}


	<V, T extends Component<C, V>> T create(Class<T> componentType, String modid, String name);

	Iterable<Component<C, ?>> getComponents();
}

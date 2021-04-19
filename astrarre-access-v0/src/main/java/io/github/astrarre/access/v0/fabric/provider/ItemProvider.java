package io.github.astrarre.access.v0.fabric.provider;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.access.v0.fabric.func.ItemFunction;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Direction;

/**
 * Use raw types if the same provider supports multiple `Access`es
 */
public interface ItemProvider<T> {
	/**
	 * @param access the provider accessing this block
	 * @return the instance, or null
	 * @see Provider#get(Access)
	 */
	@Nullable T get(Access<ItemFunction<?, T>> access, Direction direction, ItemKey key, int count, Object container);
}

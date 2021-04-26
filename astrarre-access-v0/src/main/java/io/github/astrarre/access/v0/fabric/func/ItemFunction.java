package io.github.astrarre.access.v0.fabric.func;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.util.v0.api.func.IterFunc;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Direction;

/**
 * @param <C> A container to dump and mutate the current item with, may be {@link Void}
 */
public interface ItemFunction<T, C> {
	static <T, C> IterFunc<ItemFunction<T, C>> skipIfNull(T defaultValue) {
		return arr -> (direction, key, count, container) -> {
			for (ItemFunction<T, C> function : arr) {
				T value = function.get(direction, key, count, container);
				if (value != null && value != defaultValue) {
					return value;
				}
			}
			return defaultValue;
		};
	}

	/**
	 * @param direction the side to access the item from (only really makes sense for blocks)
	 * @param key the current item at the time of access
	 * @param count the size of the stack at the time of access
	 * @param c the container to pull or push remainders from (eg. empty buckets)
	 */
	T get(@Nullable Direction direction, ItemKey key, int count, C c);
}

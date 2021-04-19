package io.github.astrarre.access.v0.fabric.func;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Direction;

public interface ItemFunction<T, C> {
	/**
	 * @param direction the side to access the item from (only really makes sense for blocks)
	 * @param key the current item at the time of access
	 * @param count the size of the stack at the time of access
	 * @param container the container to pull or push remainders from (eg. empty buckets)
	 */
	T get(@Nullable Direction direction, ItemKey key, int count, C container);
}

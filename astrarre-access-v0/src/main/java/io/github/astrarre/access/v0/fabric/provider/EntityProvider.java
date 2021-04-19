package io.github.astrarre.access.v0.fabric.provider;

import io.github.astrarre.access.v0.api.Access;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;

import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.access.v0.fabric.EntityAccess;
import io.github.astrarre.access.v0.fabric.WorldAccess;
import io.github.astrarre.access.v0.fabric.func.EntityFunction;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import org.jetbrains.annotations.Nullable;

/**
 * This Provider is implemented on {@link Entity}, and serves as a provider interface for {@link EntityAccess}
 *
 * For more information on what Providers are see the {@link Provider} interface javadocs
 * @see Provider
 * @see EntityAccess
 */
public interface EntityProvider<T> {
	/**
	 * @param access the provider accessing this block
	 * @return the instance, or null
	 * @see Provider#get(Access)
	 */
	@Nullable T get(Access<EntityFunction<T>> access, Direction direction);
}

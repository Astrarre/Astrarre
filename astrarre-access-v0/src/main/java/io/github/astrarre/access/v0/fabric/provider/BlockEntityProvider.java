package io.github.astrarre.access.v0.fabric.provider;

import io.github.astrarre.access.v0.api.Access;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Direction;

import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.access.v0.fabric.WorldAccess;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import org.jetbrains.annotations.Nullable;

/**
 * This Provider is implemented on {@link BlockEntity}, and serves as a provider interface for {@link WorldAccess}
 *
 * For more information on what Providers are see the {@link Provider} interface javadocs
 * @see Provider
 * @see WorldAccess
 */
public interface BlockEntityProvider<T> {
	/**
	 * @param access the provider accessing this block
	 * @return the instance, or null
	 * @see Provider#get(Access)
	 */
	@Nullable T get(Access<WorldFunction<T>> access, Direction direction);
}

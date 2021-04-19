package io.github.astrarre.access.v0.fabric.provider;

import io.github.astrarre.access.v0.api.Access;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.access.v0.fabric.WorldAccess;
import io.github.astrarre.access.v0.fabric.func.ItemFunction;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;
import org.jetbrains.annotations.Nullable;

/**
 * This Provider is implemented on {@link Block}, and serves as a provider interface for {@link WorldAccess}
 *
 * For more information on what Providers are see the {@link Provider} interface javadocs
 * Use raw types if the same provider supports multiple `Access`es
 * @see Provider
 * @see WorldAccess
 */
public interface BlockProvider {

	/**
	 * @param access the provider accessing this block
	 * @return the instance, or null
	 * @see Provider#get(Access)
	 */
	@Nullable Object get(Access<?> access, Direction direction, BlockState state, World view, BlockPos pos);
}

package io.github.astrarre.access.v0.api.util;

import io.github.astrarre.access.v0.api.Provider;
import io.github.astrarre.access.v0.api.func.Access;
import io.github.astrarre.access.v0.api.func.WorldFunction;
import io.github.astrarre.v0.block.Block;
import io.github.astrarre.v0.block.BlockState;
import io.github.astrarre.v0.util.math.BlockPos;
import io.github.astrarre.v0.util.math.Direction;
import io.github.astrarre.v0.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * This interface serves as a way for Providers to get objects from Blocks, implement this in your Block classes
 */
public interface BlockProvider {
	static <T> WorldFunction<T> getWorldFunction(Provider<? extends Access<T>, T> provider) {
		return (WorldFunction.NoBlockEntity<T>) (direction, state, view, pos) -> {
			Block block = state.getBlock();
			if(block instanceof BlockProvider) {
				return ((BlockProvider) block).get(provider, direction, state, view, pos);
			}
			return null;
		};
	}

	/**
	 * @param provider the provider accessing this block
	 * @return the instance, or null
	 */
	@Nullable <T> T get(Provider<? extends Access<T>, T> provider, Direction direction, BlockState state, World view, BlockPos pos);
}

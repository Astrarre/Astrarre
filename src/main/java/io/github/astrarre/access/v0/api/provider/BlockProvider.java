package io.github.astrarre.access.v0.api.provider;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.func.Returns;
import io.github.astrarre.v0.block.BlockState;
import io.github.astrarre.v0.util.math.BlockPos;
import io.github.astrarre.v0.util.math.Direction;
import io.github.astrarre.v0.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * This interface serves as a way for Providers to get objects from Blocks, implement this in your Block classes
 */
public interface BlockProvider {

	/**
	 * @param access the provider accessing this block
	 * @return the instance, or null
	 */
	@Nullable Object get(Access<?> access, Direction direction, BlockState state, World view, BlockPos pos);
}

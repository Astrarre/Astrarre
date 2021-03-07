package io.github.astrarre.access.v0.fabric.provider;

import io.github.astrarre.access.v0.api.Access;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
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

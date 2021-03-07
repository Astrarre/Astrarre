package io.github.astrarre.access.internal.access;

import java.util.function.BiFunction;


import io.github.astrarre.access.v0.fabric.cache.BlockCache;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface WorldChunkAccess {
	BlockCache astrarre_getOrCreate(BlockPos pos, BiFunction<World, BlockPos, BlockCache> newInstance);
}

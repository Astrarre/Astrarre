package io.github.astrarre.access.internal.astrarre.access;

import java.util.function.BiFunction;


import io.github.astrarre.access.v0.api.cache.CachedWorldQuery;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface WorldChunkAccess {
	CachedWorldQuery astrarre_getOrCreate(BlockPos pos, BiFunction<World, BlockPos, CachedWorldQuery> newInstance);
}

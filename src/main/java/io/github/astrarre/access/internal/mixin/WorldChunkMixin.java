package io.github.astrarre.access.internal.mixin;

import java.util.Map;
import java.util.function.BiFunction;

import com.google.common.collect.MapMaker;
import io.github.astrarre.access.internal.access.WorldChunkAccess;
import io.github.astrarre.access.v0.api.cache.CachedWorldQuery;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Mixin (WorldChunk.class)
public class WorldChunkMixin implements WorldChunkAccess {
	private final Map<BlockPos, CachedWorldQuery> onChanged = new MapMaker().weakValues().makeMap();
	@Shadow @Final private World world;

	@Inject (method = "setBlockState",
			at = @At (value = "INVOKE",
					target = "Lnet/minecraft/block/BlockState;onStateReplaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;" + "Lnet" +
					         "/minecraft/block/BlockState;Z)V"))
	private void setBlockState(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
		CachedWorldQuery access = this.onChanged.get(pos);
		if (access != null) {
			access.invalidateBlockState();
		}
	}

	@Override
	public CachedWorldQuery astrarre_getOrCreate(BlockPos pos, BiFunction<World, BlockPos, CachedWorldQuery> newInstance) {
		return this.onChanged.computeIfAbsent(pos, p -> newInstance.apply(this.world, pos));
	}
}

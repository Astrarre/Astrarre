package io.github.astrarre.event.internal.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.chunk.BlockEntityTickInvoker;

@Mixin (targets = "net.minecraft.world.chunk.WorldChunk$WrappedBlockEntityTickInvoker")
public interface WrappedBlockEntityTickInvokerAccess {
	@Accessor
	BlockEntityTickInvoker getWrapped();
}

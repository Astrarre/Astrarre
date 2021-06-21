package io.github.astrarre.event.internal.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.entity.BlockEntity;

@Mixin (targets = "net.minecraft.world.chunk.WorldChunk$DirectBlockEntityTickInvoker")
public interface DirectBlockEntityTickInvokerAccess {
	@Accessor
	BlockEntity getBlockEntity();
}

package io.github.astrarre.components.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(ReadOnlyChunk.class)
public interface ReadOnlyChunkAccess {
	@Accessor
	WorldChunk getWrapped();
}

package io.github.astrarre.components.internal.mixin;

import io.github.astrarre.components.internal.access.ProtoChunkAccess;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.chunk.ProtoChunk;

@Mixin(ProtoChunk.class)
public class ProtoChunkMixin_AccessImpl implements ProtoChunkAccess {
	NbtCompound compound;

	@Override
	public void astrarre_setProtoTag(NbtCompound compound) {
		this.compound = compound;
	}

	@Override
	public NbtCompound astrarre_getProtoTag() {
		return this.compound;
	}
}

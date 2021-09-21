package io.github.astrarre.components.internal.access;

import net.minecraft.nbt.NbtCompound;

public interface ProtoChunkAccess {
	void astrarre_setProtoTag(NbtCompound compound);

	NbtCompound astrarre_getProtoTag();
}

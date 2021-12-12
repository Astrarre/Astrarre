package io.github.astrarre.components.internal.mixin;

import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.internal.access.ProtoChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.poi.PointOfInterestStorage;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin_ChunkComponents {
	@Inject(method = "deserialize", at = @At("RETURN"))
	private static void deserializer(ServerWorld world,
			PointOfInterestStorage poiStorage,
			ChunkPos pos,
			NbtCompound nbt,
			CallbackInfoReturnable<ProtoChunk> cir) {
		ProtoChunk chunk = cir.getReturnValue();
		NbtCompound tag = nbt.getCompound("astrarre_chunkData");
		if(chunk instanceof ReadOnlyChunkAccess c) {
			WorldChunk worldChunk = c.getWrapped();
			ComponentsInternal.read(tag, worldChunk, ComponentsInternal.SERIALIZE_CHUNK_INTERNAL);
		} else if(chunk instanceof ProtoChunkAccess a) {
			a.astrarre_setProtoTag(tag);
		}
	}

	@Inject(method = "serialize", at = @At("RETURN"))
	private static void deserialize(ServerWorld world, Chunk chunk, CallbackInfoReturnable<NbtCompound> cir) {
		NbtCompound nbt;
		if(chunk instanceof ProtoChunkAccess a) {
			nbt = a.astrarre_getProtoTag();
		} else if(chunk instanceof ReadOnlyChunkAccess a) {
			nbt = ComponentsInternal.write(a.getWrapped(), ComponentsInternal.SERIALIZE_CHUNK_INTERNAL);
		} else if(chunk instanceof WorldChunk c) {
			nbt = ComponentsInternal.write(c, ComponentsInternal.SERIALIZE_CHUNK_INTERNAL);
		} else {
			return;
		}

		NbtCompound compound = cir.getReturnValue();
		compound.put("astrarre_chunkData", nbt);
	}
}

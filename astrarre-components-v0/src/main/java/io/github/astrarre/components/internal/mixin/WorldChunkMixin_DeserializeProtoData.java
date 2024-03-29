package io.github.astrarre.components.internal.mixin;

import java.io.IOException;
import java.util.function.Consumer;

import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.internal.access.ProtoChunkAccess;
import io.github.astrarre.components.internal.lazyAsm.standard.CopyAccess;
import io.github.astrarre.components.v0.api.factory.DataObjectHolder;
import io.netty.buffer.Unpooled;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(WorldChunk.class)
public class WorldChunkMixin_DeserializeProtoData implements DataObjectHolder {
	CopyAccess object;
	int version;

	@Inject(method = "<init>(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/ProtoChunk;" +
	                 "Lnet/minecraft/world/chunk/WorldChunk$EntityLoader;)V",
			at = @At("RETURN"))
	public void protoInit(ServerWorld $$0, ProtoChunk $$1, WorldChunk.EntityLoader $$2, CallbackInfo ci) {
		NbtCompound tag = ((ProtoChunkAccess) $$1).astrarre_getProtoTag();
		if(tag != null) {
			ComponentsInternal.read(tag, (WorldChunk) (Object) this, ComponentsInternal.SERIALIZE_CHUNK_INTERNAL);
		}
	}

	@Inject(method = "loadFromPacket", at = @At("HEAD"))
	public void load(PacketByteBuf buf, NbtCompound heightMap, Consumer<ChunkData.BlockEntityVisitor> bitSet, CallbackInfo ci) throws IOException {
		NbtList chunkData = heightMap.getList("astrarre_networkDataChunk", NbtElement.BYTE_ARRAY_TYPE);
		if(chunkData != null) {
			heightMap.remove("astrarre_networkDataChunk");
			for(NbtElement element : chunkData) {
				NbtByteArray array = (NbtByteArray) element;
				PacketByteBuf tempBuf = new PacketByteBuf(Unpooled.wrappedBuffer(array.getByteArray()));
				ComponentsInternal.deserializeChunk(tempBuf, (WorldChunk) (Object) this);
			}
		}
	}

	@Inject(method = "loadFromPacket", at = @At("RETURN"))
	public void load0(PacketByteBuf $$0, NbtCompound $$1, Consumer<ChunkData.BlockEntityVisitor> $$2, CallbackInfo ci) throws IOException {
		NbtList list = $$1.getList("astrarre_networkDataBE", NbtElement.BYTE_ARRAY_TYPE);
		if(list != null) {
			for(NbtElement element : list) {
				NbtByteArray array = (NbtByteArray) element;
				PacketByteBuf buf = new PacketByteBuf(Unpooled.wrappedBuffer(array.getByteArray()));
				ComponentsInternal.deserializeBlockEntity(buf, (WorldChunk) (Object) this);
			}
		}
		$$1.remove("astrarre_networkDataBE");
	}

	@Override
	public CopyAccess astrarre_getObject() {
		return this.object;
	}

	@Override
	public int astrarre_getVersion() {
		return this.version;
	}

	@Override
	public void astrarre_setObject(CopyAccess object, int version) {
		this.object = object;
		this.version = version;
	}
}

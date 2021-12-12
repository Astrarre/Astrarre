package io.github.astrarre.components.internal.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.fabric.FabricByteSerializer;
import io.github.astrarre.components.v0.fabric.builder.BlockEntityComponentBuilder;
import io.github.astrarre.components.v0.fabric.builder.ChunkComponentBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(ChunkData.class)
public class ChunkDataS2CPacketMixin_SyncBlockEntity {
	@Shadow @Final private NbtCompound heightmap;

	@Inject(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;)V", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void onAdd(WorldChunk chunk, CallbackInfo ci) {
		NbtList blockEntityData = new NbtList();
		for(BlockEntity value : chunk.getBlockEntities().values()) {
			for (var entry : ComponentsInternal.SYNC_BLOCK_ENTITY_INTERNAL.entrySet()) {
				var pair = entry.getValue();
				CustomPayloadS2CPacket packet = BlockEntityComponentBuilder.sync(ComponentsInternal.SYNC_BLOCK_ENTITY,
						pair.getSecond(),
						(Component) pair.getFirst(),
						value,
						false);
				byte[] asBytes = packet.getData().getWrittenBytes();
				blockEntityData.add(new NbtByteArray(asBytes));
			}
		}
		this.heightmap.put("astrarre_networkDataBE", blockEntityData);
	}

	@Inject(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;)V", at = @At("RETURN"))
	public void writeChunkData(WorldChunk chunk, CallbackInfo ci) {
		NbtList chunkData = new NbtList();
		for (var entry : ComponentsInternal.SYNC_CHUNK_INTERNAL.entrySet()) {
			var pair = entry.getValue();
			CustomPayloadS2CPacket packet = ChunkComponentBuilder.sync(ComponentsInternal.SYNC_CHUNK,
			                                                           pair.getSecond(),
			                                                           (Component) pair.getFirst(),
			                                                           chunk,
			                                                           false);
			byte[] asBytes = packet.getData().getWrittenBytes();
			chunkData.add(new NbtByteArray(asBytes));
		}
		this.heightmap.put("astrarre_networkDataChunk", chunkData);
	}
}

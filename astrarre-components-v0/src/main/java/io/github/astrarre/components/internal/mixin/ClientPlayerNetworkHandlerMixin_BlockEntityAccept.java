package io.github.astrarre.components.internal.mixin;

import java.io.IOException;
import java.util.Map;

import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.fabric.FabricComponents;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayerNetworkHandlerMixin_BlockEntityAccept {
	@Inject(method = "onChunkData", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void decode(ChunkDataS2CPacket packet, CallbackInfo ci, int i, int j, BiomeArray biomeArray, WorldChunk worldChunk) throws IOException {
		for(NbtCompound nbtCompound : packet.getBlockEntityTagList()) {
			NbtList list = nbtCompound.getList("astrarre_networkDataBE", NbtElement.BYTE_ARRAY_TYPE);
			if(list != null) {
				nbtCompound.remove("astrarre_networkDataBE");
				for(NbtElement element : list) {
					NbtByteArray array = (NbtByteArray) element;
					PacketByteBuf buf = new PacketByteBuf(Unpooled.wrappedBuffer(array.getByteArray()));
					ComponentsInternal.deserializeBlockEntity(buf, worldChunk);
				}
			}
		}
	}

}

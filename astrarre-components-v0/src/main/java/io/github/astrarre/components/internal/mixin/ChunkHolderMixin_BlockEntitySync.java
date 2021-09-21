package io.github.astrarre.components.internal.mixin;

import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.fabric.FabricByteSerializer;
import io.github.astrarre.components.v0.fabric.builder.BlockEntityComponentBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin_BlockEntitySync {
	@Shadow protected abstract void sendPacketToPlayersWatching(Packet<?> packet, boolean onlyOnWatchDistanceEdge);

	@Inject(method = "sendBlockEntityUpdatePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;toUpdatePacket()Lnet/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket;"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void onSync(World world, BlockPos pos, CallbackInfo ci, BlockEntity blockEntity) {
		for (var entry : ComponentsInternal.SYNC_BLOCK_ENTITY_INTERNAL.entrySet()) {
			var pair = entry.getValue();
			Packet<?> packet = BlockEntityComponentBuilder.sync(ComponentsInternal.SYNC_BLOCK_ENTITY,
					pair.getSecond(),
					(Component) pair.getFirst(),
					blockEntity,
					false);
			this.sendPacketToPlayersWatching(packet, false);
		}
	}
}

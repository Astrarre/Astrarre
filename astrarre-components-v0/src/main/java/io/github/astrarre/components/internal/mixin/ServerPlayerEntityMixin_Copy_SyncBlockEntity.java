package io.github.astrarre.components.internal.mixin;

import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.fabric.FabricByteSerializer;
import io.github.astrarre.components.v0.fabric.FabricComponents;
import io.github.astrarre.components.v0.fabric.builder.BlockEntityComponentBuilder;
import io.github.astrarre.util.v0.api.func.Copier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin_Copy_SyncBlockEntity extends PlayerEntityMixin_ObjectHolder_Serialization {
	@Shadow public ServerPlayNetworkHandler networkHandler;

	@Inject(method = "copyFrom", at = @At("HEAD"))
	public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		for (var pair : ComponentsInternal.COPY_PLAYER_ALWAYS) {
			FabricComponents.copy(oldPlayer, (Entity) (Object) this, (Component)pair.getFirst(), pair.getSecond());
		}
	}

	@Inject(method = "copyFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setHealth(F)V"))
	public void copyFromAlive(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		for (var pair : ComponentsInternal.COPY_PLAYER_ALIVE) {
			FabricComponents.copy(oldPlayer, (Entity) (Object) this, (Component)pair.getFirst(), pair.getSecond());
		}
		for (var pair : ComponentsInternal.COPY_ENTITY_INTENRAL) {
			FabricComponents.copy(oldPlayer, (Entity) (Object) this, (Component)pair.getFirst(), pair.getSecond());
		}
	}

	@Inject(method = "copyFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setScore(I)V", ordinal = 1))
	public void copyFromInventory(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		for (var pair : ComponentsInternal.COPY_PLAYER_INVENTORY) {
			FabricComponents.copy(oldPlayer, (Entity) (Object) this, (Component)pair.getFirst(), pair.getSecond());
		}
	}

	@Inject(method = "sendBlockEntityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;toUpdatePacket()Lnet/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket;"))
	public void toUpdate(BlockEntity blockEntity, CallbackInfo ci) {
		for (var entry : ComponentsInternal.SYNC_BLOCK_ENTITY_INTERNAL.entrySet()) {
			var pair = entry.getValue();
			Packet<?> packet = BlockEntityComponentBuilder.sync(ComponentsInternal.SYNC_BLOCK_ENTITY,
					pair.getSecond(),
					(Component) pair.getFirst(),
					blockEntity,
					false);
			this.networkHandler.sendPacket(packet);
		}
	}
}

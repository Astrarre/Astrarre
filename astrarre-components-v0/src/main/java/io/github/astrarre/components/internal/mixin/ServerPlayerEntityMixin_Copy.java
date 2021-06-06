package io.github.astrarre.components.internal.mixin;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.v0.api.Copier;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.fabric.FabricComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin_Copy extends PlayerEntityMixin_ObjectHolder_Serialization {
	@Inject(method = "copyFrom", at = @At("HEAD"))
	public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		for (Pair<Component<PlayerEntity, ?>, Copier<?>> pair : ComponentsInternal.COPY_PLAYER_ALWAYS) {
			FabricComponents.copy(oldPlayer, (Entity) (Object) this, (Component)pair.getFirst(), pair.getSecond());
		}
	}

	@Inject(method = "copyFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setHealth(F)V"))
	public void copyFromAlive(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		for (Pair<Component<PlayerEntity, ?>, Copier<?>> pair : ComponentsInternal.COPY_PLAYER_ALIVE) {
			FabricComponents.copy(oldPlayer, (Entity) (Object) this, (Component)pair.getFirst(), pair.getSecond());
		}
	}

	@Inject(method = "copyFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setScore(I)V", ordinal = 1))
	public void copyFromInventory(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		for (Pair<Component<PlayerEntity, ?>, Copier<?>> pair : ComponentsInternal.COPY_PLAYER_INVENTORY) {
			FabricComponents.copy(oldPlayer, (Entity) (Object) this, (Component)pair.getFirst(), pair.getSecond());
		}
	}
}

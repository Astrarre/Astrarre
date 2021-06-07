package io.github.astrarre.components.internal.mixin;

import java.io.IOException;
import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.fabric.FabricByteSerializer;
import io.github.astrarre.components.v0.fabric.FabricComponents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
@Mixin (ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin_SyncAccept {
	@Shadow @Final private MinecraftClient client;

	@Inject (method = "onCustomPayload",
			at = @At (value = "INVOKE",
					target = "Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;getChannel()Lnet/minecraft/util/Identifier;"),
			cancellable = true)
	private void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) throws IOException {
		Identifier channel = packet.getChannel();
		PacketByteBuf data = packet.getData();

		Object context = null;
		Map<String, Pair<Component<?, ?>, FabricByteSerializer<?>>> map = null;

		World currentWorld = this.client.world;
		if (currentWorld != null) {
			if (ComponentsInternal.SYNC_PLAYER.equals(channel)) {
				Entity entity = currentWorld.getEntityById(data.readInt());
				if (entity instanceof PlayerEntity) {
					context = entity;
					map = (Map) ComponentsInternal.SYNC_PLAYER_INTERNAL;
				}
				ci.cancel();
			} else if (ComponentsInternal.SYNC_ENTITY.equals(channel)) {
				Entity entity = currentWorld.getEntityById(data.readInt());
				if (entity != null) {
					context = entity;
					map = (Map) ComponentsInternal.SYNC_ENTITY_INTERNAL;
				}
				ci.cancel();
			}
		}

		if (context != null) {
			String key = data.readString();
			Pair<Component<?, ?>, FabricByteSerializer<?>> pair = map.get(key);
			if (pair != null) {
				FabricComponents.deserialize(data, context, (Component) pair.getFirst(), pair.getSecond());
			}
		}
	}
}

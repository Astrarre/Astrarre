package io.github.astrarre.components.internal.mixin;

import java.util.Map;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.fabric.builder.EntityComponentBuilder;
import io.github.astrarre.components.v0.fabric.FabricByteSerializer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.server.network.EntityTrackerEntry;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin_Sync {
	@Shadow @Final private Entity entity;

	@Inject(method = "sendPackets", at = @At("HEAD"))
	public void sync(Consumer<Packet<?>> sender, CallbackInfo ci) {
		if(!this.entity.world.isClient) {
			for (Map.Entry<String, Pair<Component<Entity, ?>, FabricByteSerializer<?>>> entry : ComponentsInternal.SYNC_ENTITY_INTERNAL.entrySet()) {
				Pair<Component<Entity, ?>, FabricByteSerializer<?>> pair = entry.getValue();
				Packet<?> packet = EntityComponentBuilder.sync(ComponentsInternal.SYNC_ENTITY,
						pair.getSecond(),
						(Component) pair.getFirst(),
						this.entity,
						false);
				sender.accept(packet);
			}

			if(this.entity instanceof PlayerEntity) {
				for (Map.Entry<String, Pair<Component<PlayerEntity, ?>, FabricByteSerializer<?>>> entry : ComponentsInternal.SYNC_PLAYER_INTERNAL.entrySet()) {
					Pair<Component<PlayerEntity, ?>, FabricByteSerializer<?>> pair = entry.getValue();
					Packet<?> packet = EntityComponentBuilder.sync(ComponentsInternal.SYNC_PLAYER,
							pair.getSecond(),
							(Component) pair.getFirst(),
							this.entity,
							false);
					sender.accept(packet);
				}
			}
		}
	}
}

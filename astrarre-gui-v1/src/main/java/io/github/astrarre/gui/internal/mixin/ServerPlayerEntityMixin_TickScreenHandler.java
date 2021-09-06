package io.github.astrarre.gui.internal.mixin;

import com.mojang.authlib.GameProfile;
import io.github.astrarre.gui.internal.access.TickingPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin_TickScreenHandler extends PlayerEntity {
	public ServerPlayerEntityMixin_TickScreenHandler(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void onTick(CallbackInfo ci) {
		((TickingPanel)this.currentScreenHandler).astrarre_tick();
	}
}

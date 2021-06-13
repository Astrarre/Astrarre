package io.github.astrarre.event.internal.entity.mixin;

import io.github.astrarre.event.v0.api.entity.PlayerContexts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

@Mixin(value = ServerPlayerInteractionManager.class, priority = 10000)
public class ServerPlayerInteractionManagerMixin_Push {
	@Inject(method = "interactBlock", at = @At("HEAD"))
	public void onInteract(ServerPlayerEntity player,
			World world,
			ItemStack stack,
			Hand hand,
			BlockHitResult hitResult,
			CallbackInfoReturnable<ActionResult> cir) {
		PlayerContexts.INTERACT_BLOCK.set(player);
	}
}

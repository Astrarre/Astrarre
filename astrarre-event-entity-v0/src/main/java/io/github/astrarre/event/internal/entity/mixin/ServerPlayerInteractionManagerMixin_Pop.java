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

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin_Pop {
	// seperated due to priorities

	@Inject (method = "interactBlock", at = @At ("RETURN"))
	public void onInteractEnd(ServerPlayerEntity player,
			World world,
			ItemStack stack,
			Hand hand,
			BlockHitResult hitResult,
			CallbackInfoReturnable<ActionResult> cir) {
		System.out.println("pop");
		PlayerContexts.INTERACT_BLOCK.pop(player);
	}
}

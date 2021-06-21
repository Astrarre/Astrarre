package io.github.astrarre.event.internal.entity.mixin;

import io.github.astrarre.event.v0.fabric.entity.EntityContexts;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin_Pop {
	// seperated due to priorities

	@Shadow @Final protected ServerPlayerEntity player;

	@Inject (method = "interactBlock", at = @At ("RETURN"))
	public void onInteractEnd(ServerPlayerEntity player,
			World world,
			ItemStack stack,
			Hand hand,
			BlockHitResult hitResult,
			CallbackInfoReturnable<ActionResult> cir) {
		EntityContexts.INTERACT_BLOCK.pop(player);
	}

	@Inject(method = "interactItem", at = @At("RETURN"))
	public void onInteract(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		EntityContexts.INTERACT_ITEM.pop(player);
	}

	@Inject(method = "tryBreakBlock", at = @At("RETURN"))
	public void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		EntityContexts.BREAK_BLOCK.pop(this.player);
	}
}

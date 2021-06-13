package io.github.astrarre.event.internal.entity.mixin;

import io.github.astrarre.event.v0.fabric.entity.PlayerContexts;
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

@Mixin(value = ServerPlayerInteractionManager.class, priority = 10000)
public class ServerPlayerInteractionManagerMixin_Push {
	@Shadow @Final protected ServerPlayerEntity player;

	@Inject(method = "interactBlock", at = @At("HEAD"))
	public void onInteract(ServerPlayerEntity player,
			World world,
			ItemStack stack,
			Hand hand,
			BlockHitResult hitResult,
			CallbackInfoReturnable<ActionResult> cir) {
		PlayerContexts.INTERACT_BLOCK.set(player);
	}

	@Inject(method = "interactItem", at = @At("HEAD"))
	public void onInteractItem(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		PlayerContexts.INTERACT_ITEM.set(player);
	}

	@Inject(method = "tryBreakBlock", at = @At("HEAD"))
	public void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		PlayerContexts.BREAK_BLOCK.set(this.player);
	}
}

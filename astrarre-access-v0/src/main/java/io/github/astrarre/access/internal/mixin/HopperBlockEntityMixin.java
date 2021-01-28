package io.github.astrarre.access.internal.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin (HopperBlockEntity.class)
public class HopperBlockEntityMixin {
	@Inject (method = "getInventoryAt(Lnet/minecraft/world/World;DDD)Lnet/minecraft/inventory/Inventory;",
			at = @At (value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private static void blockEntityLess(World world,
			double x,
			double y,
			double z,
			CallbackInfoReturnable<@Nullable Inventory> cir,
			Inventory inventory,
			BlockPos pos,
			BlockState state,
			Block block) {
		if(!block.hasBlockEntity()) {
			//Providers.INVENTORY_REGISTRY.get().get()
		}
	}
}

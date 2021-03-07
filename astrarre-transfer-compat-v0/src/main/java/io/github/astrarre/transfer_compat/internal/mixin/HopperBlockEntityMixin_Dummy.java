package io.github.astrarre.transfer_compat.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.world.World;

/**
 * dummy mixin
 */
@Mixin (HopperBlockEntity.class)
public class HopperBlockEntityMixin_Dummy {
	@Inject(method = "getInventoryAt(Lnet/minecraft/world/World;DDD)Lnet/minecraft/inventory/Inventory;", at = @At("HEAD"))
	private static void test(World world, double x, double y, double z, CallbackInfoReturnable<Inventory> cir) {}
}

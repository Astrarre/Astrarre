package io.github.astrarre.transfer.internal.mixin;

import java.util.Collections;
import java.util.function.Supplier;

import io.github.astrarre.access.v0.api.func.WorldFunction;
import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.transfer.internal.TransferInternalAstrarre;
import io.github.astrarre.transfer.v0.api.AstrarreParticipants;
import io.github.astrarre.transfer.v0.api.Participant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.world.World;

@Mixin (HopperBlockEntity.class)
public class HopperBlockEntityMixin {
	/**
	 * an access to the astrarre world registry
	 */
	private static final Supplier<WorldFunction<Participant<ItemKey>>> ASTRARRE_WORLD_FUNCTION = AstrarreParticipants.ITEM_WORLD
			                                                                                             .getExcluding(Collections.singleton(
					                                                                                             TransferInternalAstrarre.FROM_INVENTORY));

	@Inject (method = "getInventoryAt(Lnet/minecraft/world/World;DDD)Lnet/minecraft/inventory/Inventory;",
			at = @At (value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)" +
					         "Lnet/minecraft/block/entity/BlockEntity;"),
			locals = LocalCapture.PRINT)
	private static void test(World world, double x, double y, double z, CallbackInfoReturnable<Inventory> cir, long test) {

	}
}

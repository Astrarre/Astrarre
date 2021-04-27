package io.github.astrarre.transfer.v0.tr.mixin;

import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.transfer.v0.tr.RebornCoreCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import reborncore.common.blockentity.FluidConfiguration;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.util.Tank;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(value = FluidConfiguration.class, remap = false)
public class FluidConfiguration_HookGetTank {

	@Inject(method = "getTank", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void print(MachineBaseBlockEntity machine, Direction facing, CallbackInfoReturnable<Tank> cir, BlockPos pos, BlockEntity blockEntity) {
		BlockState state;
		World world;
		if(blockEntity != null) {
			state = blockEntity.getCachedState();
			world = blockEntity.getWorld();
		} else {
			state = machine.getCachedState();
			world = machine.getWorld();
		}
		Participant<Fluid> participant = FabricParticipants.FLUID_WORLD.get().get(facing.getOpposite(), state, world, pos, blockEntity);
		if(participant == (Object)Participants.EMPTY) {
			return;
		}
		Tank tank = RebornCoreCompat.TO_TANK.get().apply(participant);
		if(tank != null) {
			cir.setReturnValue(tank);
		}
	}
}

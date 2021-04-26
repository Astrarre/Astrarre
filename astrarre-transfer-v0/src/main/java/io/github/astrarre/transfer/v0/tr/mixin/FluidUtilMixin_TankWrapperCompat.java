package io.github.astrarre.transfer.v0.tr.mixin;

import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.tr.TankParticipant;
import io.github.astrarre.transfer.v0.tr.TankWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reborncore.common.fluid.FluidUtil;
import reborncore.common.fluid.FluidValue;
import reborncore.common.util.Tank;

import net.minecraft.fluid.Fluid;

@Mixin(value = FluidUtil.class, remap = false)
public class FluidUtilMixin_TankWrapperCompat {
	@Inject(method = "transferFluid", at = @At("HEAD"), cancellable = true)
	private static void transferFluid(Tank source, Tank destination, FluidValue amount, CallbackInfo ci) {
		Participant<Fluid> from, to;
		if(!(source instanceof TankWrapper) && !(destination instanceof TankWrapper)) {
			return;
		}

		if(destination instanceof TankWrapper) {
			to = ((TankWrapper) destination).participant;
		} else {
			to = new TankParticipant(destination);
		}

		if(source instanceof TankWrapper) {
			from = ((TankWrapper) source).participant;
		} else {
			from = new TankParticipant(destination);
		}

		Participants.move(Transaction.GLOBAL, from, to, amount.getRawValue() * 81, true);
	}
}

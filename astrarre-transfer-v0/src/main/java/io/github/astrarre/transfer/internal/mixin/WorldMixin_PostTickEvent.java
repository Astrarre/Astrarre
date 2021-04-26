package io.github.astrarre.transfer.internal.mixin;

import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.World;

@Mixin (value = World.class, priority = 2_000_000_000)
public abstract class WorldMixin_PostTickEvent {
	@Inject (at = @At ("RETURN"), method = "tickBlockEntities")
	protected void tickWorldAfterBlockEntities(CallbackInfo ci) {
		Transaction active = Transaction.active();
		if (active != null) {
			throw new IllegalStateException("Transaction was not closed before end of tick", active.getInitialization());
		}
	}
}

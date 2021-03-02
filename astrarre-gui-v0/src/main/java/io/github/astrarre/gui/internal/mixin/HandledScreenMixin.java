package io.github.astrarre.gui.internal.mixin;

import io.github.astrarre.gui.internal.access.ExtraSlotAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

@Mixin (HandledScreen.class)
public abstract class HandledScreenMixin extends ScreenMixin {
	@Inject (method = "isPointOverSlot", at = @At ("HEAD"), cancellable = true)
	public void customPointOverSlot(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir) {
		if (slot instanceof ExtraSlotAccess) {
			boolean is = ((ExtraSlotAccess) slot).astrarre_isPointOverSlot(pointX, pointY);
			cir.setReturnValue(is);
		}
	}
}

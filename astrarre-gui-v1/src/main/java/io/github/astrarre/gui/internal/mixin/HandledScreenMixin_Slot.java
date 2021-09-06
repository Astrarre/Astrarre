package io.github.astrarre.gui.internal.mixin;

import io.github.astrarre.gui.v1.api.component.ASlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin_Slot extends ScreenMixin_Access {
	@Shadow protected int x;
	@Shadow protected int y;
	boolean noPoint;

	@Inject(method = "getSlotAt", at = @At("HEAD"), cancellable = true)
	public void getSlotAt(double x, double y, CallbackInfoReturnable<Slot> cir) {
		if(this.panel != null) {
			var slot = this.panel.getAtRecursive((float) x, (float) y);
			if(slot instanceof ASlot a) {
				cir.setReturnValue(a.slot);
			}
		}
	}

	@Inject(method = "drawSlot", at = @At("HEAD"), cancellable = true)
	public void onDrawSlot(MatrixStack matrices, Slot slot, CallbackInfo ci) {
		if(slot instanceof ASlot.Minecraft) {
			ci.cancel();
		}
	}

	@ModifyArg(method = "render",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;isPointOverSlot(Lnet/minecraft/screen/slot/Slot;DD)Z"))
	public Slot skipRender(Slot slot) {
		if(slot instanceof ASlot.Minecraft) {
			this.noPoint = true;
		}
		return slot;
	}

	@Inject(method = "isPointOverSlot", at = @At("HEAD"), cancellable = true)
	public void isPointOverSlot(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir) {
		if(this.noPoint) {
			this.noPoint = false;
			cir.setReturnValue(false);
		} else if(slot instanceof ASlot.Minecraft a) {
			var s = a.slot();
			if(s.screen.getAtRecursive((float) pointX, (float) pointY) == s) {
				cir.setReturnValue(true);
			}
		}
	}

	@Inject(method = "isClickOutsideBounds", at = @At("HEAD"), cancellable = true)
	public void isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir) {
		if(this.panel != null && this.panel.isMouseOver(mouseX, mouseY)) {
			cir.setReturnValue(false);
		}
	}
}

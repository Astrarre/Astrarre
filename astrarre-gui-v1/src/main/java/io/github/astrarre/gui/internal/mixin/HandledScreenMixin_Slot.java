package io.github.astrarre.gui.internal.mixin;

import io.github.astrarre.gui.v1.api.component.slot.ASlot;
import io.github.astrarre.gui.v1.api.component.slot.ASlotInternalAccess;
import io.github.astrarre.gui.internal.slot.SlotAdapter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin_Slot extends ScreenMixin_Access {
	@Shadow protected int x;
	@Shadow protected int y;

	@Shadow protected abstract boolean isPointOverSlot(Slot slot, double pointX, double pointY);

	@Shadow @Nullable protected Slot focusedSlot;
	boolean noPoint;

	@Inject(method = "getSlotAt", at = @At("HEAD"), cancellable = true)
	public void getSlotAt(double x, double y, CallbackInfoReturnable<Slot> cir) {
		if(this.panel != null) {
			var slot = this.panel.getAtRecursive((float) x, (float) y);
			if(slot instanceof ASlot a) {
				cir.setReturnValue(ASlotInternalAccess.getSlot(a));
			}
		}
	}

	@Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
	public void getMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if(this.panel != null && this.panel.mouseReleased(mouseX, mouseY, button)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "drawSlot", at = {
			@At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"),
			@At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableDepthTest()V")
	}, cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	public void onDrawSlot(MatrixStack matrices, Slot slot, CallbackInfo ci, int i, int j, ItemStack renderStack, boolean highlightOverride) {
		if(slot instanceof SlotAdapter a) {
			ASlotInternalAccess.setRender(a.slot(), renderStack, highlightOverride);
			ci.cancel();
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/util/math/MatrixStack;II)V"))
	public void onRenderForeground(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if(this.panel != null) {
			var slot = this.panel.getAtRecursive(mouseX, mouseY);
			if(slot instanceof ASlot a) {
				this.focusedSlot = ASlotInternalAccess.getSlot(a);
			}
		}
	}

	@ModifyArg(method = "render",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;isPointOverSlot(Lnet/minecraft/screen/slot/Slot;DD)Z"))
	public Slot skipRender(Slot slot) {
		if(slot instanceof SlotAdapter) {
			this.noPoint = true;
		}
		return slot;
	}

	@Inject(method = "isPointOverSlot", at = @At("HEAD"), cancellable = true)
	public void isPointOverSlot(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir) {
		if(this.noPoint) {
			this.noPoint = false;
			cir.setReturnValue(false);
		} else if(slot instanceof SlotAdapter a) {
			var s = a.slot();
			if(this.panel.getAtRecursive((float) pointX, (float) pointY) == s) {
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

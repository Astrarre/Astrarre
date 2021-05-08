package io.github.astrarre.gui.internal.mixin;


import io.github.astrarre.gui.internal.PanelElement;
import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ExtraSlotAccess;
import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.containers.ScreenHandlerContainer;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

@Mixin (HandledScreen.class)
public abstract class HandledScreenMixin extends ScreenMixin {

	@Inject (method = "drawSlot",
			at = @At (value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;setZOffset(I)V", ordinal = 0),
			cancellable = true,
			locals = LocalCapture.CAPTURE_FAILHARD)
	public void drawSlot(MatrixStack matrices, Slot slot, CallbackInfo ci, int i, int j, ItemStack itemStack, boolean highlighted, boolean skipRender) {
		if (slot instanceof ExtraSlotAccess) {
			if(!skipRender) {
				((ExtraSlotAccess) slot).setHighlighted(highlighted);
				((ExtraSlotAccess)slot).setOverride(itemStack);
				((ExtraSlotAccess)slot).setRender(true);
			}

			ci.cancel();
		}
	}

	@ModifyArg (method = "render", at = @At (value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;isPointOverSlot(Lnet/minecraft/screen/slot/Slot;DD)Z"), index = 1)
	public double redirect(Slot slot, double x, double y) {
		if(slot instanceof ExtraSlotAccess) {
			ExtraSlotAccess access = (ExtraSlotAccess) slot;
			if(access.astrarre_isPointOverSlot(x, y)) {
				access.setHighlighted(true);
				this.focusedSlot = slot;
			}
			return 1_000_000;
		}
		return x;
	}

	@Inject (method = "mouseReleased", at = @At ("HEAD"), cancellable = true)
	public void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		for (Element child : this.children()) {
			if (child instanceof PanelElement) {
				if (child.mouseReleased(mouseX, mouseY, button)) {
					cir.setReturnValue(true);
					return;
				}
			}
		}
	}

	@Inject (method = "mouseDragged", at = @At ("HEAD"), cancellable = true)
	public void mouseReleased(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
		for (Element child : this.children()) {
			if (child instanceof PanelElement) {
				if (child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
					cir.setReturnValue(true);
					return;
				}
			}
		}
	}

	@Inject (method = "isPointOverSlot", at = @At ("HEAD"), cancellable = true)
	public void isPointOverSlot(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir) {
		if (slot instanceof ExtraSlotAccess) {
			if (((ExtraSlotAccess) slot).astrarre_isPointOverSlot(pointX, pointY)) {
				cir.setReturnValue(true);
			} else {
				cir.setReturnValue(false);
			}
		}
	}

	@Override
	public RootContainerInternal getRoot() {
		ScreenHandlerContainer container = (ScreenHandlerContainer) ((ScreenRootAccess) this.getScreenHandler()).getRoot();
		container.screen = (HandledScreen<?>) (Object) this;
		if(container.resizeList != null) {
			this.resizes.addAll(container.resizeList);
			for (RootContainer.OnResize resize : container.resizeList) {
				resize.resize(this.width, this.height);
			}
			container.resizeList.clear();
		}
		return container;
	}

	@Shadow
	public abstract ScreenHandler getScreenHandler();

	@Shadow @Nullable protected Slot focusedSlot;

	@Override
	public void readRoot(NBTagView buf) {
		throw new UnsupportedOperationException();
	}
}

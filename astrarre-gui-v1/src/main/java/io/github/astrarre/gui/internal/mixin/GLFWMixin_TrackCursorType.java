package io.github.astrarre.gui.internal.mixin;

import java.util.List;

import io.github.astrarre.gui.internal.CursorImpl;
import io.github.astrarre.gui.internal.GuiInternal;
import io.github.astrarre.gui.v1.api.listener.cursor.CursorType;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GLFW.class, remap = false)
public abstract class GLFWMixin_TrackCursorType {
	@Inject(method = "glfwSetCursor", at = @At("HEAD"))
	private static void setCursor(long window, long cursor, CallbackInfo ci) {
		if(window != GuiInternal.Holder.WINDOW_HANDLE) {
			GuiInternal.LOGGER.warn("Multiple GLFW windows open at once! Astrarre's GUI implementation only supports one at a time.");
		} else {
			CursorImpl.type = GuiInternal.HANDLE_TO_TYPE.computeIfAbsent(cursor, l -> () -> GLFW.glfwSetCursor(window, l));
		}
	}

	@Inject(method = "glfwCreateStandardCursor", at = @At("RETURN"))
	private static void trackCursor(int shape, CallbackInfoReturnable<Long> cir) {
		List<CursorType.Standard> standard = CursorType.Standard.VALUES;
		if(shape <= standard.size()) {
			standard.get(shape).setHandle(cir.getReturnValueJ());
		} else {
			GuiInternal.LOGGER.warn("Unknown standard GLFW cursor type " + (shape - GLFW.GLFW_ARROW_CURSOR) + " + " + GLFW.GLFW_ARROW_CURSOR);
		}
	}
}

package io.github.astrarre.gui.internal.mixin;

import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ContainerAccess;
import io.github.astrarre.gui.internal.containers.ScreenRootContainer;
import io.github.astrarre.rendering.internal.MatrixGraphics;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(Screen.class)
public class ScreenMixin implements ContainerAccess {
	private RootContainerInternal internal;

	@Inject(method = "render", at = @At("HEAD"))
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if(this.internal != null) {
			try {
				Graphics3d g3d = new MatrixGraphics(matrices);
				this.internal.getContentPanel().render(g3d, delta);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	@Override
	public RootContainerInternal getContainer() {
		if(this.internal == null) {
			this.internal = this.createContainer();
		}
		return this.internal;
	}

	protected RootContainerInternal createContainer() {
		return new ScreenRootContainer<>((Screen) (Object) this);
	}
}

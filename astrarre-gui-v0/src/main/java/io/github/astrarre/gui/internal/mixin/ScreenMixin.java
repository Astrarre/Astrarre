package io.github.astrarre.gui.internal.mixin;

import java.util.List;

import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.containers.ScreenRootContainer;
import io.github.astrarre.gui.internal.PanelElement;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.rendering.internal.MatrixGraphics;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

@Mixin (Screen.class)
public abstract class ScreenMixin implements ScreenRootAccess, ParentElement {
	@Shadow @Final protected List<Element> children;

	protected RootContainerInternal internal;
	private PanelElement panel;

	@Inject (method = "render", at = @At ("HEAD"))
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		RootContainerInternal internal = this.getRoot();
		if (internal != null) {
			try {
				Graphics3d g3d = new MatrixGraphics(matrices);
				internal.getContentPanel().render(g3d, delta);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	@Inject (method = "init()V", at = @At ("HEAD"))
	public void init(CallbackInfo ci) {
		this.attachListener(this.getRoot());
	}

	private RootContainerInternal attachListener(RootContainerInternal internal) {
		if (internal != null) {
			this.children.add(this.panel = new PanelElement(internal.getContentPanel(), internal));
		}
		return internal;
	}

	@Override
	public RootContainerInternal getRoot() {
		return this.internal;
	}

	@Override
	public void readRoot(Input input) {
		this.internal = new ScreenRootContainer<>((Screen) (Object) this, input);
	}

	@Override
	public void astrarre_focusPanel() {
		this.setFocused(this.panel);
	}
}

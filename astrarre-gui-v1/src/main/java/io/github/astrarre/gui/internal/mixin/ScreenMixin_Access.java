package io.github.astrarre.gui.internal.mixin;

import io.github.astrarre.gui.internal.ElementRootPanel;
import io.github.astrarre.gui.internal.access.PanelScreenAccess;
import io.github.astrarre.gui.v1.api.component.ARootPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public abstract class ScreenMixin_Access implements PanelScreenAccess {
	@Shadow protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

	@Shadow public int width;
	@Shadow public int height;
	public ElementRootPanel.ScreenImpl panel;

	@Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("RETURN"))
	public void onInit(MinecraftClient client, int width, int height, CallbackInfo ci) {
		if(this.panel != null) {
			this.panel.setBoundsInternal(this.width, this.height);
			this.addDrawableChild(this.panel);
		}
	}

	@Override
	public ARootPanel getRootPanel() {
		if(this.panel == null) {
			this.setRootPanel(new ElementRootPanel.ScreenImpl((Screen) (Object) this));
		}
		return this.panel;
	}

	@Override
	public void setRootPanel(ARootPanel panel) {
		this.panel = (ElementRootPanel.ScreenImpl) panel;
		this.panel.setBoundsInternal(this.width, this.height);
		this.addDrawableChild(this.panel);
	}

	@Inject(method = "onClose", at = @At("HEAD"))
	public void onClose(CallbackInfo ci) {
		if(this.panel != null) {
			this.panel.onClose.get().run();
		}
	}
}

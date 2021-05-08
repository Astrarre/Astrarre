package io.github.astrarre.gui.internal.mixin;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.gui.internal.PanelElement;
import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ResizeListenerAccess;
import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.containers.ScreenRootContainer;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.fabric.graphics.FabricGUIGraphics;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.rendering.internal.DummyScreen;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;

@Mixin (Screen.class)
public abstract class ScreenMixin implements ScreenRootAccess, ParentElement, ResizeListenerAccess {
	protected final List<RootContainer.OnResize> resizes = new ArrayList<>();
	@Shadow public int width;
	@Shadow public int height;
	@Shadow @Final protected List<Element> children;
	protected RootContainerInternal internal;
	private PanelElement panel;

	@Inject (method = "render", at = @At ("HEAD"))
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		RootContainerInternal internal = this.getRoot();
		if (internal != null) {
			try {
				boolean enabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
				if(!enabled) {
					RenderSystem.enableDepthTest();
				}
				GuiGraphics g3d = new FabricGUIGraphics(matrices, (Screen) (Object) this);
				internal.getContentPanel().mouseHover(internal, mouseX, mouseY);
				internal.getContentPanel().render(internal, g3d, delta);
				g3d.flush();
				if(!enabled) {
					RenderSystem.disableDepthTest();
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	@Override
	public RootContainerInternal getRoot() {
		return this.internal;
	}

	@Override
	public RootContainerInternal getClientRoot() {
		if (this.internal == null) {
			this.internal = new ScreenRootContainer<>((Screen) (Object) this);
			this.attachListener(this.internal);
		}
		return this.internal;
	}

	@Override
	public void readRoot(NBTagView input) {
		this.internal = new ScreenRootContainer<>((Screen) (Object) this, input);
	}

	@Override
	public void astrarre_focusPanel() {
		this.setFocused(this.panel);
	}

	@Inject (method = "tick", at = @At ("HEAD"))
	public void onTick(CallbackInfo ci) {
		if (this.panel != null) {
			this.panel.tick();
		}
	}

	@Inject (method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At ("HEAD"))
	public void init(MinecraftClient client, int width, int height, CallbackInfo ci) {
		if ((Object)this != DummyScreen.INSTANCE) {
			DummyScreen.INSTANCE.init(client, width, height);
		}
		for (RootContainer.OnResize resize : this.resizes) {
			resize.resize(width, height);
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
	public void addResizeListener(RootContainer.OnResize resize) {
		this.resizes.add(resize);
	}
}

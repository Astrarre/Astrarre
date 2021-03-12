package io.github.astrarre.testmod.gui;

import javax.swing.JButton;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.panel.Panel;
import io.github.astrarre.gui.v0.swing.adapter.ComponentAdapter;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.internal.MatrixGraphics;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class TestClientOnlyGui {
	public static void clientOnly() {
		RootContainer container = RootContainer.openClientOnly();
		Panel panel = container.getContentPanel();
		JButton button = new JButton();
		button.setSize(100, 100);
		panel.addClient(new ComponentAdapter(null, button) {});
	}

	public static class BoundedDrawable extends Drawable {
		public BoundedDrawable() {
			super(null);
		}

		@Override
		protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
			MatrixStack stack = ((MatrixGraphics) graphics).matrices;
			RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, true);
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			RenderSystem.colorMask(false, false, false, false);
			RenderSystem.depthMask(false);
			RenderSystem.stencilFunc(GL11.GL_ALWAYS, 1, 0xff);
			RenderSystem.stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
			RenderSystem.stencilMask(0xff);
			DrawableHelper.fill(stack, 10, 10, 20, 20, 0xffffffff);

			RenderSystem.colorMask(true, true, true, true);
			RenderSystem.depthMask(true);
			RenderSystem.stencilMask(0x00);
			RenderSystem.stencilFunc(GL11.GL_EQUAL, 1, 0xFF);

			DrawableHelper.fill(stack, 0, 0, 30, 30, 0xffffffff);

			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}

		@Override
		protected void write0(RootContainer container, Output output) {

		}
	}
}

package io.github.astrarre.testmod.gui;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.panel.APanel;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.edge.Stencil;

public class TestClientOnlyGui {
	public static void clientOnly() {
		RootContainer container = RootContainer.openClientOnly();
		APanel panel = container.getContentPanel();
		panel.addClient(new BoundedDrawable());
	}

	public static class BoundedDrawable extends Drawable {
		public BoundedDrawable() {
			super(null);
		}

		@Override
		protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
			Stencil stencil = graphics.stencil();
			int stencilId = stencil.startTracingStencil();
				graphics.fillRect(20, 20, 0xffffffff);
				graphics.flush();
			stencil.drawTracingStencil(stencilId);
				int nestedStencilId = stencil.startTracingStencil();
					graphics.fillRect(10, 10, 10, 10, 0xffffffff);
					graphics.flush();
			stencil.drawTracingStencil(nestedStencilId);
					graphics.fillRect(30, 30, 0xffffffff);
					graphics.flush();
				stencil.endStencil(nestedStencilId);
			stencil.endStencil(stencilId);
		}

		@Override
		protected void write0(RootContainer container, Output output) {

		}
	}
}

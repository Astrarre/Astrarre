package io.github.astrarre.gui.v0.api.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.AggregateDrawable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;
import org.lwjgl.opengl.GL11;

public class VerticalListWidget extends AggregateDrawable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "list_widget"), VerticalListWidget::new);
	protected final int width, height;
	public VerticalListWidget(int width, int height) {
		this(ENTRY, width, height);
	}

	protected VerticalListWidget(DrawableRegistry.Entry id, int width, int height) {
		super(id);
		this.width = width;
		this.height = height;
		this.setBounds(Polygon.rectangle(width, height));
	}

	protected VerticalListWidget(DrawableRegistry.Entry id, Input input) {
		super(id, input);
		this.width = input.readInt();
		this.height = input.readInt();
	}

	private VerticalListWidget(Input input) {
		this(ENTRY, input);
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		float maxWidth = 0, y = 0;
		RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT, false);
		RenderSystem.stencilMask(0xff);
		RenderSystem.stencilFunc(GL11.GL_EQUAL, 1, 0xFF);
		RenderSystem.stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
		graphics.fillRect(this.width, this.height, 0);
		RenderSystem.stencilFunc(GL11.GL_EQUAL, 1, 0xFF);
		RenderSystem.stencilMask(0x00);
		RenderSystem.disableDepthTest();
		for (Drawable drawable : this.drawables) {
			Polygon enclosing = drawable.getBounds().getEnclosing();
			float width = enclosing.getX(2), height = enclosing.getY(2);
			if(width > maxWidth) {
				maxWidth = width;
			}
			try(Close c = graphics.applyTransformation(Transformation.translate(0, y, 0))) {
				drawable.render(container, graphics, tickDelta);
			}
			y += height;
		}
		RenderSystem.stencilMask(0xFF);
		RenderSystem.stencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
		RenderSystem.enableDepthTest();
	}

	public static void init() {
	}
}

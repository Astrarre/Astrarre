package io.github.astrarre.rendering.internal;

import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.plane.TextRenderer;
import io.github.astrarre.rendering.v1.api.space.Render3d;

import net.minecraft.text.Text;

public record WrappedLabelIcon(TextRenderer renderer, Text text, float width, int color, boolean shadow) implements Icon {
	@Override
	public float height() {
		return this.renderer.wrap(this.text, (int) this.width).size() * this.renderer.textHeight() + 1;
	}

	@Override
	public void render(Render3d render) {
		render.text(this.color, 0, 0, this.shadow).renderWrappedText(this.text, (int) this.width);
	}
}

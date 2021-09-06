package io.github.astrarre.rendering.v1.api.plane.icon;

import java.util.function.IntSupplier;

import io.github.astrarre.rendering.v1.api.plane.TextRenderer;
import io.github.astrarre.rendering.v1.api.space.Render3d;

import net.minecraft.text.Text;

public record ScrollingLabelIcon(Text text, int argb, boolean shadow, float width, IntSupplier offsetX) implements Icon {
	@Override
	public float height() {
		return 10;
	}

	@Override
	public void render(Render3d render) {
		TextRenderer renderer = render.text(this.argb, 0, 0, this.shadow);
		if(renderer.width(this.text) > this.width) {
			renderer.renderScrollingText(this.text, this.offsetX.getAsInt(), this.width, true);
		} else {
			renderer.render(this.text);
		}
	}
}

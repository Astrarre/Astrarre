package io.github.astrarre.gui.v1.api.component.icon;

import java.util.Objects;
import java.util.function.IntSupplier;

import io.github.astrarre.rendering.v1.api.space.Render3d;

import net.minecraft.text.Text;

public final class ScrollingLabelIcon implements Icon {
	private final Text text;
	private final int argb;
	private final boolean shadow;
	private final float width;
	private final IntSupplier offsetX;

	public ScrollingLabelIcon(Text text, int argb, boolean shadow, float width, IntSupplier offsetX) {
		this.text = text;
		this.argb = argb;
		this.shadow = shadow;
		this.width = width;
		this.offsetX = offsetX;
	}

	@Override
	public float height() {
		return 10;
	}

	@Override
	public float width() { return width; }

	@Override
	public void render(Render3d render) {
		render.text(this.argb, 0, 0, this.shadow).renderScrollingText(this.text, this.offsetX.getAsInt(), this.width, true);
	}

	public Text text() { return text; }

	public int argb() { return argb; }

	public boolean shadow() { return shadow; }

	@Override
	public int hashCode() {
		return Objects.hash(text, argb, shadow, width);
	}
}

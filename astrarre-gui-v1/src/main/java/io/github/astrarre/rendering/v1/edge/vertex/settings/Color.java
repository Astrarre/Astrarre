package io.github.astrarre.rendering.v1.edge.vertex.settings;

import io.github.astrarre.rendering.internal.BufferSupplier;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;

import net.minecraft.client.render.VertexFormats;

public final class Color<Next extends VertexSetting<?>> extends VertexSetting<Next> {
	static final Type<Color<?>> TYPE = type(Color::new, VertexFormats.COLOR_ELEMENT);

	public Color(BufferSupplier builder, VertexFormat<?> settings, VertexSetting<?> next) {
		super(builder, settings, next);
	}


	/**
	 * red is at position zero, so 255 is max red.
	 * [red, green, blue, alpha]
	 */
	public Next rgb(int color) {
		return this.abgr(color | 0xFF000000);
	}

	public Next argb(int color) {
		int a = (color >> 24) & 0xFF, r = (color >> 16) & 0xFF, g = (color >> 8) & 0xFF, b = color & 0xFF;
		return this.argb(a, r, g, b);
	}

	/**
	 * This is the order minecraft generally uses
	 * red is at position zero, so 255 is max red.
	 * [alpha, blue, green, red]
	 */
	public Next abgr(int color) {
		int r = color & 0xFF, g = (color >> 8) & 0xFF, b = (color >> 16) & 0xFF, a = (color >> 24) & 0xFF;
		this.builder().color(r, g, b, a);
		return this.next;
	}

	public Next argb(int a, int r, int g, int b) {
		this.builder().color(r, g, b, a);
		return this.next;
	}
}

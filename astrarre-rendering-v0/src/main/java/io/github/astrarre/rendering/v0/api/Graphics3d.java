package io.github.astrarre.rendering.v0.api;

import io.github.astrarre.rendering.v0.api.util.Close;


public interface Graphics3d extends Graphics2d {
	void drawLine(float x1, float y1, float z1, float x2, float y2, float z2, int color);

	@Override
	default void drawLine(float x1, float y1, float x2, float y2, int color) {
		this.drawLine(x1, y1, 0, x2, y2, 0, color);
	}

	@Override
	default void fillRect(float x, float y, float width, float height, int color) {
		float x2 = x + width, y2 = y + height;
		this.fillRect(x, y, 0, x, y2, 0, x2, y2, 0, x2, y, 0, color);
	}

	default void drawRect(float x, float y, float width, float height, int color) {
		float x2 = x + width, y2 = y + height;
		this.drawLine(x, y, x2, y, color);
		this.drawLine(x2, y, x2, y2, color);
		this.drawLine(x2, y2, x, y2, color);
		this.drawLine(x, y2, x, y, color);
	}

	/**
	 * fill a rectangle in 3d space. The points must be coplanar and in counter clockwise order
	 */
	void fillRect(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int color);

	default Close translate(float x, float y, float z) {
		return this.applyTransformation(Transformation.translate(x, y, z));
	}
}

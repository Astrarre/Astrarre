package io.github.astrarre.rendering.v0.api.util;

public enum Axis2d {
	X,
	Y;

	public Vec2f inDirection(float val) {
		if(this.isX()) {
			return Vec2f.of(val, 0);
		} else {
			return Vec2f.of(0, val);
		}
	}

	public boolean isX() {
		return this == X;
	}

	public boolean isY() {
		return this == Y;
	}
}

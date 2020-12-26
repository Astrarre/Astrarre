package io.github.astrarre.gui.v0.api.components;

public class Point2f {
	public final float x, y;

	public Point2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "(" + this.x + ',' + this.y + ')';
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Point2f)) {
			return false;
		}

		Point2f point = (Point2f) object;

		if (Float.compare(point.x, this.x) != 0) {
			return false;
		}
		return Float.compare(point.y, this.y) == 0;
	}

	@Override
	public int hashCode() {
		int result = (this.x != +0.0f ? Float.floatToIntBits(this.x) : 0);
		result = 31 * result + (this.y != +0.0f ? Float.floatToIntBits(this.y) : 0);
		return result;
	}
}
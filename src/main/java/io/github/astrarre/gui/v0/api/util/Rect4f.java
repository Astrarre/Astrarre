package io.github.astrarre.gui.v0.api.util;

import static java.lang.Float.*;

public class Rect4f {
	public final float x, y, width, height;

	public Rect4f(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString() {
		return "(" + this.x + ',' + this.y + ')' + " [" + this.width + ',' + this.height + ']';
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Rect4f)) {
			return false;
		}

		Rect4f f = (Rect4f) object;

		if (compare(f.x, this.x) != 0) {
			return false;
		}
		if (compare(f.y, this.y) != 0) {
			return false;
		}
		if (compare(f.width, this.width) != 0) {
			return false;
		}
		return compare(f.height, this.height) == 0;
	}

	@Override
	public int hashCode() {
		int result = (this.x != +0.0f ? floatToIntBits(this.x) : 0);
		result = 31 * result + (this.y != +0.0f ? floatToIntBits(this.y) : 0);
		result = 31 * result + (this.width != +0.0f ? floatToIntBits(this.width) : 0);
		result = 31 * result + (this.height != +0.0f ? floatToIntBits(this.height) : 0);
		return result;
	}
}
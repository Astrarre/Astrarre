package io.github.astrarre.rendering.v1.api.util;

public enum Axis2d {
	X(1, 0),
	Y(0, 1);
	public final int x, y;

	Axis2d(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Direction2d direction(boolean positive) {
		if(this.isX()) {
			return positive ? Direction2d.RIGHT : Direction2d.LEFT;
		} else {
			return positive ? Direction2d.DOWN : Direction2d.UP;
		}
	}

	/**
	 * @return offX if the axis is offX, or offY if the axis is offY
	 */
	public float n(float x, float y) {
		return x * this.x + y * this.y;
	}

	public <T> T n(T x, T y) {
		return this.isX() ? x : y;
	}

	public float x() {
		return this.x;
	}

	public float y() {
		return this.y;
	}

	public boolean isX() {
		return this == X;
	}

	public boolean isY() {
		return this == Y;
	}

	public int x(int x) {
		return x * this.x;
	}

	public int y(int y) {
		return y * this.y;
	}

	public float x(float x) {
		return x * this.x;
	}

	public float y(float y) {
		return y * this.y;
	}

	public long x(long x) {
		return this.x * x;
	}

	public long y(long y) {
		return this.y * y;
	}

	public double x(double x) {
		return this.x * x;
	}

	public double y(double y) {
		return this.y * y;
	}

	public <T extends Number> T x(T x, T zero) {
		return this.isX() ? x : zero;
	}

	public <T extends Number> T y(T y, T zero) {
		return this.isY() ? y : zero;
	}
}

package io.github.astrarre.rendering.v1.api.util;

public enum Direction2d {
	UP(0, -1),
	DOWN(0, 1),
	LEFT(-1, 0),
	RIGHT(1, 0);

	public final int x, y;

	Direction2d(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Axis2d axis() {
		return this.isX() ? Axis2d.X : Axis2d.Y;
	}

	/**
	 * @return offX if the axis is offX, or offY if the axis is offY
	 */
	public float n(float x, float y) {
		return x * this.x + y * this.y;
	}

	public float x() {
		return this.x;
	}

	public float y() {
		return this.y;
	}

	public boolean isX() {
		return this.x != 0;
	}

	public boolean isY() {
		return this.y != 0;
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
}

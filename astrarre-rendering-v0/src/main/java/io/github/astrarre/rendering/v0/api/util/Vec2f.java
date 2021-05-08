package io.github.astrarre.rendering.v0.api.util;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public final class Vec2f {
	protected static final Stack<Vec2f> POOL = new ObjectArrayList<>();

	private float x, y;

	public static Vec2f of(float x, float y) {
		if(POOL.isEmpty()) {
			return new Vec2f(x, y);
		} else {
			Vec2f pooled = POOL.pop();
			pooled.x = x;
			pooled.y = y;
			return pooled;
		}
	}

	public static void returnToPool(Vec2f unused) {
		POOL.push(unused);
	}

	protected Vec2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2f withX(float x) {
		return Vec2f.of(x, this.y);
	}

	public Vec2f withY(float y) {
		return Vec2f.of(this.x, y);
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}
}

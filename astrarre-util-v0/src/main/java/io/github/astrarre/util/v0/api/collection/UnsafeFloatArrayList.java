package io.github.astrarre.util.v0.api.collection;

import it.unimi.dsi.fastutil.floats.FloatArrayList;

public class UnsafeFloatArrayList extends FloatArrayList {
	public UnsafeFloatArrayList(int capacity) {
		super(capacity);
	}

	public UnsafeFloatArrayList(float[] a, int offset, int length) {
		super(a, offset, length);
	}

	public float[] arr() {
		return this.a;
	}
}

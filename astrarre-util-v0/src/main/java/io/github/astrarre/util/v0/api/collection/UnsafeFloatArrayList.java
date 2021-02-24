package io.github.astrarre.util.v0.api.collection;

import it.unimi.dsi.fastutil.floats.FloatArrayList;

public class UnsafeFloatArrayList extends FloatArrayList {
	public UnsafeFloatArrayList(int capacity) {
		super(capacity);
	}

	public float[] arr() {
		return this.a;
	}
}

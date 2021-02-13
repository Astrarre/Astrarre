package io.github.astrarre.gui.internal.util;

import java.util.Arrays;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatCollection;

public class UnsafeFloatArrayList extends FloatArrayList {
	public UnsafeFloatArrayList(int capacity) {
		super(capacity);
	}

	public float[] arr() {
		return this.a;
	}
}

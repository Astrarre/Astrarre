package io.github.astrarre.recipies.v0.api.util;

public class Val<V> {
	public static Val<Double> ofDouble() {
		return new Val<>(0d);
	}

	public static Val<Double> ofDouble(double d) {
		return new Val<>(d);
	}

	public static Val<Float> ofFloat() {
		return new Val<>(0f);
	}

	public static Val<Float> ofFloat(float d) {
		return new Val<>(d);
	}

	public static Val<Long> ofLong() {
		return new Val<>(0L);
	}

	public static Val<Long> ofLong(long d) {
		return new Val<>(d);
	}

	public static Val<Integer> ofInteger() {
		return new Val<>(0);
	}

	public static Val<Integer> ofInteger(int d) {
		return new Val<>(d);
	}
	
	protected V val;

	public Val(V val) {
		this.val = val;
	}

	public Val() {
	}

	public V get() {
		return this.val;
	}

	public void set(V val) {
		this.val = val;
	}
}

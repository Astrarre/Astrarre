package io.github.astrarre.components.v0.api.components;

public interface IntComponent<C> extends PrimitiveComponent<C, Integer> {
	@Override
	default Integer get(C context) {
		return this.getInt(context);
	}

	@Override
	default void set(C context, Integer val) {
		this.setInt(context, val);
	}

	int getInt(C context);

	void setInt(C context, int val);
}
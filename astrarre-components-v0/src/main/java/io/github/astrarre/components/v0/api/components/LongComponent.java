package io.github.astrarre.components.v0.api.components;

public interface LongComponent<C> extends PrimitiveComponent<C, Long> {
	@Override
	default Long get(C context) {
		return this.getLong(context);
	}

	@Override
	default void set(C context, Long val) {
		this.setLong(context, val);
	}

	long getLong(C context);

	void setLong(C context, long val);
}
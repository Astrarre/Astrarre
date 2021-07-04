package io.github.astrarre.components.v0.api.components;

public interface FloatComponent<C> extends PrimitiveComponent<C, Float> {
	@Override
	default Float get(C context) {
		return this.getFloat(context);
	}

	@Override
	default void set(C context, Float val) {
		this.setFloat(context, val);
	}

	float getFloat(C context);

	void setFloat(C context, float val);
}
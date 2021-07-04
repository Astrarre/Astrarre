package io.github.astrarre.components.v0.api.components;

public interface DoubleComponent<C> extends PrimitiveComponent<C, Double> {
	@Override
	default Double get(C context) {
		return this.getDouble(context);
	}

	@Override
	default void set(C context, Double val) {
		this.setDouble(context, val);
	}

	double getDouble(C context);

	void setDouble(C context, double val);
}
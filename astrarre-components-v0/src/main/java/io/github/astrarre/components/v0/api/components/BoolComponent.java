package io.github.astrarre.components.v0.api.components;

public interface BoolComponent<C> extends PrimitiveComponent<C, Boolean> {
	@Override
	default Boolean get(C context) {
		return this.getBool(context);
	}

	@Override
	default void set(C context, Boolean val) {
		this.setBool(context, val);
	}

	boolean getBool(C context);

	void setBool(C context, boolean val);
}
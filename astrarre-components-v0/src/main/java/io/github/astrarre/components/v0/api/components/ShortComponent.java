package io.github.astrarre.components.v0.api.components;

public interface ShortComponent<C> extends PrimitiveComponent<C, Short> {
	@Override
	default Short get(C context) {
		return this.getShort(context);
	}

	@Override
	default void set(C context, Short val) {
		this.setShort(context, val);
	}

	short getShort(C context);

	void setShort(C context, short val);
}
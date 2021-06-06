package io.github.astrarre.components.v0.api.components;

public interface %1$sComponent<C> extends PrimitiveComponent<C, %2$s> {
	@Override
	default %2$s get(C context) {
		return this.get%1$s(context);
	}

	@Override
	default void set(C context, %2$s val) {
		this.set%1$s(context, val);
	}

	%3$s get%1$s(C context);

	void set%1$s(C context, %3$s val);
}
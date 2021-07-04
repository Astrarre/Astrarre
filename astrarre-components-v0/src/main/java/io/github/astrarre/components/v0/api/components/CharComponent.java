package io.github.astrarre.components.v0.api.components;

public interface CharComponent<C> extends PrimitiveComponent<C, Character> {
	@Override
	default Character get(C context) {
		return this.getChar(context);
	}

	@Override
	default void set(C context, Character val) {
		this.setChar(context, val);
	}

	char getChar(C context);

	void setChar(C context, char val);
}
package io.github.astrarre.components.v0.api.components;

public interface ByteComponent<C> extends PrimitiveComponent<C, Byte> {
	@Override
	default Byte get(C context) {
		return this.getByte(context);
	}

	@Override
	default void set(C context, Byte val) {
		this.setByte(context, val);
	}

	byte getByte(C context);

	void setByte(C context, byte val);
}
package io.github.astrarre.networking.v0.api;

public interface BufferOperator<T> {
	void set(Input input, T array, int index);
}
package io.github.astrarre.util.v0.api;

public interface SafeCloseable extends AutoCloseable {
	@Override
	void close();
}

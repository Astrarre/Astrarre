package io.github.astrarre.v0.api.rendering.util;

public interface Closeable extends AutoCloseable {
	@Override
	void close();
}

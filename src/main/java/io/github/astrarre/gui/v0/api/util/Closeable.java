package io.github.astrarre.gui.v0.api.util;

public interface Closeable extends AutoCloseable {
	@Override
	void close();
}

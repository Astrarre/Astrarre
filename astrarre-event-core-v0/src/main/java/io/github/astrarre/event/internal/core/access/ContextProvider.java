package io.github.astrarre.event.internal.core.access;

public interface ContextProvider {

	Object get();

	void set(Object val);
}

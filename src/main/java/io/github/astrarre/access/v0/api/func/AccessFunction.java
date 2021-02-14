package io.github.astrarre.access.v0.api.func;

import java.util.function.Function;

public interface AccessFunction<A, B> extends Returns<B>, Function<A, B> {
	static <A, B> AccessFunction<A, B> empty() {
		return a -> null;
	}
}

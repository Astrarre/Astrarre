package io.github.astrarre.internal;

import java.util.function.Function;

public interface ThrowingFunction<A, B> extends Function<A, B> {
	B applyThrow(A a) throws Throwable;

	@Override
	default B apply(A a) {
		try {
			return this.applyThrow(a);
		} catch (Throwable throwable) {
			throw new IllegalStateException(throwable);
		}
	}
}

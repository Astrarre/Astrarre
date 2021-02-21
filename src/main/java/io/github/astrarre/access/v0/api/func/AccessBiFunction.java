package io.github.astrarre.access.v0.api.func;

import java.util.function.BiFunction;

public interface AccessBiFunction<A, B, C> extends Returns<C>, BiFunction<A, B, C> {}

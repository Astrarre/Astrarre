package io.github.astrarre.access.v0.api.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.util.v0.api.func.IterFunc;

/**
 * You don't have to extend this class to make a helper, it's just a utility class
 */
public abstract class AbstractInputAccessHelper<I, F> {
	public final IterFunc<F> iterFunc;
	public final Consumer<Function<I, F>> andThen;
	public final F empty;

	public AbstractInputAccessHelper(AbstractInputAccessHelper<I, F> copyFrom) {
		this(copyFrom.iterFunc, copyFrom.andThen, copyFrom.empty);
	}

	public AbstractInputAccessHelper(IterFunc<F> func, Consumer<Function<I, F>> adder, F empty) {
		this.iterFunc = func;
		this.andThen = adder;
		this.empty = empty;
	}
}

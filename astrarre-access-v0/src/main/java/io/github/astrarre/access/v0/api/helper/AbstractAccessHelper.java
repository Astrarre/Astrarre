package io.github.astrarre.access.v0.api.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.util.FilteredFunc;
import io.github.astrarre.util.v0.api.func.FuncFinder;
import io.github.astrarre.util.v0.api.func.IterFunc;

/**
 * You don't have to extend this class to make a helper, it's just a utility class
 */
public abstract class AbstractAccessHelper<I, F> {
	public final IterFunc<F> iterFunc;
	public final FilteredFunc.Adding<I, F> andThen;
	public final F empty;

	public AbstractAccessHelper(AccessHelpers.Context<I, F> copyFrom) {
		this.iterFunc = copyFrom.func();
		this.andThen = copyFrom.andThen();
		this.empty = copyFrom.empty();
	}
}

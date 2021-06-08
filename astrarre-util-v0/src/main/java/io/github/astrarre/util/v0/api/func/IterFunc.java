package io.github.astrarre.util.v0.api.func;

import java.util.Arrays;

/**
 * combines a list of items into one item
 */
@FunctionalInterface
public interface IterFunc<A> extends ArrayFunc<A> {
	/**
	 * finds the first non-null value in the list, otherwise returns null
	 */
	IterFunc NON_NULL = arr -> {
		for (Object a : arr) {
			if(a != null) {
				return a;
			}
		}
		return null;
	};

	@Override
	default A combine(A[] array) {
		return this.combine(Arrays.asList(array));
	}

	A combine(Iterable<A> arr);
}

package io.github.astrarre.util.v0.api.func;

import com.google.common.collect.Iterables;

public interface ArrayFunc<A> {
	/**
	 * finds the first non-null value in the list, otherwise returns null
	 */
	ArrayFunc NON_NULL = arr -> {
		for (Object a : arr) {
			if(a != null) {
				return a;
			}
		}
		return null;
	};

	A combine(A[] array);

	default IterFunc<A> asIter(Class<A> type) {
		return arr -> this.combine(Iterables.toArray(arr, type));
	}
}

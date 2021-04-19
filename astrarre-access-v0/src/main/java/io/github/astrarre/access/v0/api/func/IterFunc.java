package io.github.astrarre.access.v0.api.func;

/**
 * combines a list of items into one item
 */
@FunctionalInterface
public interface IterFunc<A> {
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



	A combine(Iterable<A> arr);
}

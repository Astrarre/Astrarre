package io.github.astrarre.itemview.internal.util;

import java.util.Comparator;
import java.util.Iterator;

import org.jetbrains.annotations.Nullable;

public record ImmutableIterable<T>(Iterator<T> delegate) implements Iterator<T> {
	@Override
	public boolean hasNext() {
		return this.delegate.hasNext();
	}

	@Override
	public T next() {
		return this.delegate.next();
	}

	public static <T> int compare(Iterator<T> a, Iterator<T> b, @Nullable Comparator<T> t) {
		while(a.hasNext() && b.hasNext()) {
			T av = a.next(), bv = b.next();
			int comparison;
			if(av instanceof Comparable c) {
				comparison = c.compareTo(bv);
			} else {
				comparison = t.compare(av, bv);
			}

			if(comparison != 0) {
				return comparison;
			}
		}
		if(a.hasNext()) {
			return 1;
		}
		if(b.hasNext()) {
			return -1;
		}
		return 0;
	}
}

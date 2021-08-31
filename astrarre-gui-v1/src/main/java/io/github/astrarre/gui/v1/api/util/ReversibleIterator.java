package io.github.astrarre.gui.v1.api.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.SortedSet;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import org.jetbrains.annotations.NotNull;

public interface ReversibleIterator<T> extends Iterator<T> {
	static <T> ReversibleIterator<T> list(List<T> list) {
		return list(list.listIterator());
	}

	static <T> ReversibleIterator<T> iter(Iterable<@NotNull T> iter) {
		if(iter instanceof SortedSet<T> s) {
			return sortedSet(s);
		} else if(iter instanceof List<T> s) {
			return list(s);
		} else {
			return new IterableReversableIterator<>(iter);
		}
	}

	static <T> ReversibleIterator<T> list(ListIterator<T> iterator) {
		return new ReversibleIterator<T>() {
			@Override
			public T next() {
				return iterator.next();
			}

			@Override
			public T previous() {
				return iterator.previous();
			}

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public boolean hasPrevious() {
				return iterator.hasPrevious();
			}
		};
	}
	
	static <T> ReversibleIterator<T> sortedSet(SortedSet<@NotNull T> set) {
		return new ReversibleIterator<>() {
			T current;

			@Override
			public T next() {
				if(this.current == null) return this.current = set.first();

				Iterator<T> iterator = set.tailSet(this.current).iterator();
				iterator.next();
				return this.current = iterator.next();
			}

			@Override
			public T previous() {
				if(this.current == null) return this.current = set.last();

				SortedSet<T> head = set.headSet(this.current);
				return this.current = head.last();
			}

			@Override
			public boolean hasNext() {
				return set.last() != this.current;
			}

			@Override
			public boolean hasPrevious() {
				return set.first() != this.current;
			}
		};
	}
	
	@Override
	T next();
	
	T previous();
	
	@Override
	boolean hasNext();
	
	boolean hasPrevious();
	
	default void toStart() {
		while(this.hasPrevious()) {
			this.previous();
		}
	}
	
	default void toEnd() {
		while(this.hasNext()) {
			this.next();
		}
	}

	class IterableReversableIterator<T> implements ReversibleIterator<T> {
		private final Iterable<@NotNull T> iter;
		Iterator<T> current;
		T last;

		public IterableReversableIterator(Iterable<@NotNull T> iter) {
			this.iter = iter;
			this.current = iter.iterator();
		}

		@Override
		public T next() {
			return (this.last = this.current.next());
		}

		@Override
		public T previous() {
			T last = null;
			Iterator<T> iterator = this.iter.iterator();
			while(iterator.hasNext()) {
				T curr = iterator.next();
				if(Objects.equals(curr, this.last)) {
					if(last == null) {
						throw new UnsupportedOperationException("has no previous element!");
					}
					this.current = Iterators.concat(Iterators.singletonIterator(curr), iterator);
					return (this.last = last);
				}
				last = curr;
			}

			return (this.last = Iterables.getLast(this.iter));
		}

		@Override
		public boolean hasNext() {
			return this.current.hasNext();
		}

		@Override
		public boolean hasPrevious() {
			T last = null;
			for(T curr : this.iter) {
				if(Objects.equals(curr, this.last)) {
					return last != null;
				}
				last = curr;
			}
			return true;
		}
	}
}

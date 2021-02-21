package io.github.astrarre.access.v0.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import io.github.astrarre.access.v0.api.func.IterFunc;
import io.github.astrarre.access.v0.api.func.Returns;
import org.jetbrains.annotations.NotNull;

public class Access<A extends Returns<T>, T> {
	protected final IterFunc<A> combiner;
	protected List<Object> delegates = new ArrayList<>();
	protected Consumer<Access<A, T>> listener;
	protected A compiledFunction;

	/**
	 * @param combiner andThen
	 */
	public Access(IterFunc<A> combiner) {
		this.combiner = combiner;
		this.recompile();
	}

	protected void recompile() {
		this.compiledFunction = this.combiner.combine(() -> Iterators.transform(this.delegates.iterator(), this::get));
		if (this.listener != null) {
			this.listener.accept(this);
		}
	}

	protected A get(Object o) {
		if (o instanceof Entry) {
			return (A) ((Entry<?, ?, ?, ?>) o).delegate.delegate;
		}
		return (A) o;
	}

	public <E extends Returns<U>, U> Access<A, T> dependsOn(Access<E, U> access, Function<E, A> function) {
		return this.dependsOn(access, function, true);
	}

	private <E extends Returns<U>, U> Access<A, T> dependsOn(Access<E, U> access, Function<E, A> function, boolean end) {
		// current entry in delegates, we store the reference so we can update it
		Entry[] entryRef = {null};

		{
			// compute initial entry and add it to the list
			Iterable<Object> initialInputs = access.getExcluding(this); // List#add always returns true
			entryRef[0] = new Entry<>(new CombinedFunction<>(function.apply(access.combiner.combine(Iterables.transform(initialInputs, o -> {
				if (o instanceof CombinedFunction) {
					return (E) ((CombinedFunction<?, ?>) o).delegate;
				}
				return (E) o;
			}))), initialInputs), function, access);
			if (end) {
				this.delegates.add(entryRef[0]);
			} else {
				this.delegates.add(0, entryRef[0]);
			}
			this.recompile();
		}

		access.addListener(a -> {
			// we take the now outdated (theoretically) entry
			Entry<A, T, E, U> entry = entryRef[0];
			// and compare the inputs of the entry against the new set of inputs, if the opposing access added a dependency on us, it shouldn't have
			// changed
			Iterable<Object> newInputs = a.getExcluding(this);
			if (!Iterables.elementsEqual(entry.delegate.inputs, newInputs)) {
				entry.delegate = new CombinedFunction<>(entry.mapping.apply(entry.dependency.combiner.combine(Iterables.transform(newInputs, o -> {
					if (o instanceof CombinedFunction) {
						return (E) ((CombinedFunction<?, ?>) o).delegate;
					}
					return (E) o;
				}))), newInputs);
				this.recompile();
			}
		});


		return this;
	}

	private Iterable<Object> getExcluding(Access<?, ?> access) {
		return () -> new Iterator<Object>() {
			private final Iterator<Object> iterator = Access.this.delegates.iterator();
			A cached;

			@Override
			public boolean hasNext() {
				return this.get() != null;
			}

			protected Object get() {
				if (this.cached != null) {
					return this.cached;
				}

				while (this.iterator.hasNext()) {
					Object next = this.iterator.next();
					if (next instanceof Entry) {
						if (((Entry<?, ?, ?, ?>) next).dependency == access) {
							continue;
						} else {
							return this.cached = (A) ((Entry<?, ?, ?, ?>) next).get(access);
						}
					}
					return this.cached = (A) next;
				}
				return null;
			}

			@Override
			public Object next() {
				Object a = this.get();
				this.cached = null;
				return a;
			}
		};
	}

	private Access<A, T> addListener(Consumer<Access<A, T>> listener) {
		if (this.listener == null) {
			this.listener = listener;
		} else {
			this.listener = this.listener.andThen(listener);
		}
		return this;
	}

	public <E extends Returns<U>, U> Access<A, T> dependsOnBefore(Access<E, U> access, Function<E, A> function) {
		return this.dependsOn(access, function, false);
	}

	public Access<A, T> before(A func) {
		this.delegates.add(0, func);
		this.recompile();
		return this;
	}

	/**
	 * adds a function to this access, the later you register, the higher your priority
	 */
	public Access<A, T> andThen(A func) {
		this.delegates.add(func);
		this.recompile();
		return this;
	}

	@NotNull
	public A get() {
		return this.compiledFunction;
	}

	private static final class CombinedFunction<A extends Returns<T>, T> {
		private final A delegate;
		private final Iterable<Object> inputs;

		private CombinedFunction(A delegate, Iterable<Object> inputs) {
			this.delegate = delegate;
			this.inputs = inputs;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof CombinedFunction)) {
				return false;
			}
			CombinedFunction<?, ?> function = (CombinedFunction<?, ?>) o;
			return Iterables.elementsEqual(this.inputs, function.inputs);
		}

	}

	private static final class Entry<A extends Returns<T>, T, E extends Returns<U>, U> {
		private final Function<E, A> mapping;
		private final Access<E, U> dependency;
		private CombinedFunction<A, T> delegate;

		private Entry(CombinedFunction<A, T> delegate, Function<E, A> mapping, Access<E, U> dependency) {
			this.delegate = delegate;
			this.mapping = mapping;
			this.dependency = dependency;
		}

		public Object get(Access<?, ?> excluding) {
			Iterable<Object> inputs = this.dependency.getExcluding(excluding);
			return new CombinedFunction<>(this.mapping.apply(this.dependency.combiner.combine(Iterables.transform(inputs, o -> {
				if (o instanceof CombinedFunction) {
					return (E) ((CombinedFunction<?, ?>) o).delegate;
				}
				return (E) o;
			}))), inputs);
		}
	}
}

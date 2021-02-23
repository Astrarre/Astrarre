package io.github.astrarre.access.v0.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
		if (o instanceof Access.Func) {
			return ((Func<?, A>) o).delegate;
		}
		return (A) o;
	}

	@NotNull
	public A get() {
		return this.compiledFunction;
	}

	public <E extends Returns<U>, U> Access<A, T> dependsOn(Access<E, U> access, Function<E, A> function) {
		return this.dependsOn(access, function, true);
	}

	private Access<A, T> addListener(Consumer<Access<A, T>> listener) {
		listener.accept(this);
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


	private <SilenceGenerics extends Returns<?>> Iterable<Object> getWithout(Collection<Access<?, ?>> accesses) {
		return Iterables.transform(Iterables.filter(this.delegates, o -> !(o instanceof Func && accesses.contains(((Func<?, ?>) o).dep))), delegate -> {
			if(delegate instanceof Func) {
				// our own delegate
				Func<SilenceGenerics, A> current = (Func<SilenceGenerics, A>) delegate;
				Func<SilenceGenerics, A> copied = new Func<>(current.dep, current.mapping);

				HashSet<Access<?, ?>> newAccesses = new HashSet<>(accesses);
				newAccesses.add(this);
				Iterable<Object> val = current.dep.getWithout(newAccesses);
				copied.inputs = val;
				copied.delegate = current.mapping.apply(compile(val, current.dep.combiner));
				return copied;
			}
			return delegate;
		});
	}

	private <E extends Returns<U>, U> Access<A, T> dependsOn(Access<E, U> access, Function<E, A> function, boolean end) {
		Func<E, A> dependency = new Func<>(access, function);
		if(end) this.delegates.add(dependency);
		else this.delegates.add(0, dependency);
		access.addListener(a -> {
			// todo recompile somehow
			HashSet<Access<?, ?>> accesses = new HashSet<>();
			Iterable<Object> inputs = a.getWithout(accesses);
			if(dependency.inputs == null || !Iterables.elementsEqual(inputs, dependency.inputs)) {
				dependency.inputs = inputs;
				dependency.delegate = function.apply(compile(inputs, a.combiner));
				this.recompile();
			}
		});
		return this;
	}

	private static final class Func<Entries extends Returns<?>, Target extends Returns<?>> {
		private final Access<Entries, ?> dep;
		private final Function<Entries, Target> mapping;
		private Target delegate;
		private Iterable<Object> inputs;

		private Func(Access<Entries, ?> dep, Function<Entries, Target> mapping) {
			this.dep = dep;
			this.mapping = mapping;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof Func)) {
				return false;
			}

			Func<?, ?> func = (Func<?, ?>) o;

			if (!Objects.equals(this.dep, func.dep)) {
				return false;
			}
			if (!Objects.equals(this.mapping, func.mapping)) {
				return false;
			}
			return Iterables.elementsEqual(this.inputs, func.inputs);
		}

		@Override
		public int hashCode() {
			int result = this.dep != null ? this.dep.hashCode() : 0;
			result = 31 * result + (this.mapping != null ? this.mapping.hashCode() : 0);
			result = 31 * result + (this.delegate != null ? this.delegate.hashCode() : 0);
			return result;
		}
	}

	private static <A> A compile(Iterable<Object> vals, IterFunc<A> combine) {
		return combine.combine(Iterables.transform(vals, o -> {
			if(o instanceof Func) return (A)((Func<?, ?>) o).delegate;
			return (A)o;
		}));
	}
}

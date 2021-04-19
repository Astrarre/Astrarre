package io.github.astrarre.access.v0.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import io.github.astrarre.access.v0.api.func.IterFunc;
import io.github.astrarre.access.v0.fabric.EntityAccess;
import io.github.astrarre.access.v0.fabric.WorldAccess;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * An access is essentially a list of functions, like an event handler. Like a function, it allows for any number of inputs and an output.
 * To create an access you must have some way of combining all the listeners into one, so the output can be easily accessed.
 * Access' can be circularly dependent on other Access'.
 * @param <F> a function type
 * @see BiFunctionAccess
 * @see FunctionAccess
 * @see EntityAccess
 * @see WorldAccess
 */
public class Access<F> {
	protected final IterFunc<F> combiner;
	protected List<Object> delegates = new ArrayList<>();
	protected Consumer<Access<F>> listener;
	protected F compiledFunction;

	/**
	 * @param combiner andThen
	 */
	public Access(IterFunc<F> combiner) {
		this.combiner = combiner;
		this.recompile();
	}


	@NotNull
	public F get() {
		return this.compiledFunction;
	}

	/**
	 * Calling this function multiple times is ill-advised, you should store the Supplier somewhere
	 * @return an invoker that contains all functions except those from the passed registries (the supplier should be called each time for it to be updated
	 */
	@Contract("_ -> new")
	public Supplier<F> getExcluding(Collection<Access<?>> accesses) {
		AtomicReference<F> reference = new AtomicReference<>();
		this.addListener(a -> {
			F val = compile(a.getWithout(accesses), a.combiner);
			reference.set(val);
		});
		return reference::get;
	}

	/**
	 * adds a access dependency
	 * @param access the function to depend on
	 * @return this
	 */
	@Contract("_ -> this")
	public Access<F> dependsOn(Access<F> access) {
		return this.dependsOn(access, Function.identity());
	}

	/**
	 * adds a access dependency
	 * @param access the function to depend on
	 * @param function a converter function to map the other access to this one
	 * @return this
	 */
	@Contract("_,_ -> this")
	public <E> Access<F> dependsOn(Access<E> access, Function<E, F> function) {
		return this.dependsOn(access, function, true);
	}

	/**
	 * fired when a function is added to the access
	 */
	@Contract("_ -> this")
	public Access<F> addListener(Consumer<Access<F>> listener) {
		listener.accept(this);
		if (this.listener == null) {
			this.listener = listener;
		} else {
			this.listener = this.listener.andThen(listener);
		}
		return this;
	}

	/**
	 * adds a access dependency, however it adds it to the front of this list, which means the function is called first
	 * @param access the function to depend on
	 * @param function a converter function to map the other access to this one
	 * @return this
	 */
	@Contract("_,_ -> this")
	public <E> Access<F> dependsOnBefore(Access<E> access, Function<E, F> function) {
		return this.dependsOn(access, function, false);
	}

	/**
	 * adds a function to this access, however it adds it to the front of this list, which means the function is called first
	 */
	@Contract("_ -> this")
	public Access<F> before(F func) {
		this.delegates.add(0, func);
		this.recompile();
		return this;
	}

	/**
	 * adds a function to this access, the later you register, the higher your priority
	 */
	@Contract("_ -> this")
	public Access<F> andThen(F func) {
		this.delegates.add(func);
		this.recompile();
		return this;
	}

	private <SilenceGenerics> Iterable<Object> getWithout(Collection<Access<?>> accesses) {
		return Iterables.transform(Iterables.filter(this.delegates, o -> !(o instanceof Func && accesses.contains(((Func<?, ?>) o).dep))), delegate -> {
			if(delegate instanceof Func) {
				// our own delegate
				Func<SilenceGenerics, F> current = (Func<SilenceGenerics, F>) delegate;
				Func<SilenceGenerics, F> copied = new Func<>(current.dep, current.mapping);

				HashSet<Access<?>> newAccesses = new HashSet<>(accesses);
				newAccesses.add(this);
				Iterable<Object> val = current.dep.getWithout(newAccesses);
				copied.inputs = val;
				copied.delegate = current.mapping.apply(compile(val, current.dep.combiner));
				return copied;
			}
			return delegate;
		});
	}

	private <E> Access<F> dependsOn(Access<E> access, Function<E, F> function, boolean end) {
		Func<E, F> dependency = new Func<>(access, function);
		if(end) this.delegates.add(dependency);
		else this.delegates.add(0, dependency);
		access.addListener(a -> {
			HashSet<Access<?>> accesses = new HashSet<>();
			Iterable<Object> inputs = a.getWithout(accesses);
			if(dependency.inputs == null || !Iterables.elementsEqual(inputs, dependency.inputs)) {
				dependency.inputs = inputs;
				dependency.delegate = function.apply(compile(inputs, a.combiner));
				this.recompile();
			}
		});
		return this;
	}

	private static final class Func<Entries, Target> {
		private final Access<Entries> dep;
		private final Function<Entries, Target> mapping;
		private Target delegate;
		private Iterable<Object> inputs;

		private Func(Access<Entries> dep, Function<Entries, Target> mapping) {
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

	/**
	 * recombines the listeners into one function (should be called when the delegates list is updated)
	 */
	protected void recompile() {
		this.compiledFunction = this.combiner.combine(() -> Iterators.transform(this.delegates.iterator(), this::get));
		if (this.listener != null) {
			this.listener.accept(this);
		}
	}

	protected F get(Object o) {
		if (o instanceof Access.Func) {
			return ((Func<?, F>) o).delegate;
		}
		return (F) o;
	}
}

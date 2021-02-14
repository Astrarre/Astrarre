package io.github.astrarre.access.v0.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.func.Returns;
import org.jetbrains.annotations.NotNull;

public class Access<A extends Returns<T>, T> {
	protected final BinaryOperator<A> andThen;
	protected List<Object> delegates = new ArrayList<>();
	protected Listener<A, T> listener;

	protected A defaultAccess, defaultCompiledDelegate;

	public interface Listener<A extends Returns<T>, T> {
		void onRegister(A func, Collection<Access<?, ?>> dependencies);

		default Listener<A, T> andThen(Listener<A, T> access) {
			return (func, dependencies) -> {
				this.onRegister(func, dependencies);
				access.onRegister(func, dependencies);
			};
		}
	}

	/**
	 * @param andThen andThen
	 * @param defaultAccess a version of the function that returns the default value of this provider
	 */
	public Access(BinaryOperator<A> andThen, A defaultAccess) {
		this.andThen = andThen;
		this.defaultAccess = defaultAccess;
	}

	public <B extends Returns<T>> Access<A, T> andThenMap(B accessor, Function<B, A> query) {
		return this.andThen(query.apply(accessor));
	}

	public <B extends Returns<T>> Access<A, T> andThenMapWithDependencies(B accessor, Function<B, A> query, Collection<Access<?, ?>> dependencies) {
		return this.andThenWithDependencies(query.apply(accessor), dependencies);
	}

	public <U, B extends Returns<U>> Access<A, T> andThenMapFunc(B accessor, Function<B, A> query) {
		return this.andThen(query.apply(accessor));
	}

	public <U, B extends Returns<U>> Access<A, T> andThenMapFuncWithDependencies(B accessor, Function<B, A> query, Collection<Access<?, ?>> dependencies) {
		return this.andThenWithDependencies(query.apply(accessor), dependencies);
	}

	/**
	 * this provider will inherit all listeners from the target provider (circular dependencies should work)
	 */
	public void addDependency(Access<A, T> access) {
		if(access == this) throw new IllegalArgumentException("cannot depend on self!");
		access.registerListener((func, dependencies) -> {
			if(!dependencies.contains(this)) {
				Set<Access<?, ?>> deps = new HashSet<>(dependencies);
				deps.add(access);
				this.andThenWithDependencies(func, deps);
			}
		});
	}

	public <B extends Returns<T>> void addDependency(Access<B, T> access, Function<B, A> query) {
		if(access == this) throw new IllegalArgumentException("cannot depend on self!");
		access.registerListener((func, dependencies) -> {
			if(!dependencies.contains(this)) {
				Set<Access<?, ?>> deps = new HashSet<>(dependencies);
				deps.add(access);
				this.andThenWithDependencies(query.apply(func), deps);
			}
		});
	}

	public <U, B extends Returns<U>> void addDependencyType(Access<B, U> access, Function<B, A> query) {
		if(access == this) throw new IllegalArgumentException("cannot depend on self!");
		access.registerListener((func, dependencies) -> {
			if(!dependencies.contains(this)) {
				Set<Access<?, ?>> deps = new HashSet<>(dependencies);
				deps.add(access);
				this.andThenWithDependencies(query.apply(func), deps);
			}
		});
	}

	public void registerListener(Listener<A, T> listener) {
		for (Object delegate : this.delegates) {
			if(delegate instanceof Entry) {
				listener.onRegister((A) ((Entry<?, ?>) delegate).delegate, ((Entry<?, ?>) delegate).dependencies);
			} else {
				listener.onRegister((A) delegate, Collections.emptyList());
			}
		}

		if(this.listener == null) {
			this.listener = listener;
		} else {
			this.listener = this.listener.andThen(listener);
		}
	}

	public Access<A, T> andThenWithDependencies(A func, Collection<Access<?, ?>> dependencies) {
		this.add(func);
		this.delegates.add(new Entry<>(func, dependencies));
		if(this.listener != null) {
			this.listener.onRegister(func, dependencies);
		}
		return this;
	}

	protected void add(A func) {
		if (this.defaultCompiledDelegate == this.defaultAccess) {
			this.defaultCompiledDelegate = func;
		} else if (this.defaultCompiledDelegate == null) {
			this.defaultCompiledDelegate = func;
		} else {
			this.defaultCompiledDelegate = this.andThen.apply(this.defaultCompiledDelegate, func);
		}
	}

	public Access<A, T> andThen(A func) {
		this.add(func);
		this.delegates.add(func);
		if(this.listener != null) {
			this.listener.onRegister(func, Collections.emptyList());
		}

		return this;
	}

	@NotNull
	public A get() {
		return this.defaultCompiledDelegate == null ? this.defaultAccess : this.defaultCompiledDelegate;
	}

	private static final class Entry<A extends Returns<T>, T> {
		private final A delegate;
		private final Collection<Access<?, ?>> dependencies;

		private Entry(A delegate, Collection<Access<?, ?>> dependencies) {
			this.delegate = delegate;
			this.dependencies = dependencies;
		}
	}
}

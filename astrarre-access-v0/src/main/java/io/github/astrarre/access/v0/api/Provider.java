package io.github.astrarre.access.v0.api;

import java.util.function.BinaryOperator;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.func.Access;
import org.jetbrains.annotations.NotNull;

public class Provider<A extends Access<T>, T> {
	protected final BinaryOperator<A> andThen;
	protected A defaultAccess, delegate;

	/**
	 * @param andThen andThen
	 * @param defaultAccess a version of the function that returns null
	 */
	public Provider(BinaryOperator<A> andThen, A defaultAccess) {
		this.andThen = andThen;
		this.defaultAccess = defaultAccess;
	}

	public <B extends Access<T>> Provider<A, T> wraps(B accessor, Function<B, A> query) {
		return this.andThen(query.apply(accessor));
	}

	public Provider<A, T> andThen(A func) {
		if (this.delegate == this.defaultAccess) {
			this.delegate = func;
		} else if (this.delegate == null) {
			this.delegate = func;
		} else {
			this.delegate = this.andThen.apply(this.delegate, func);
		}

		return this;
	}

	@NotNull
	public A get() {
		return this.delegate == null ? this.defaultAccess : this.delegate;
	}
}

package io.github.astrarre.util.v0.api.func.predicate;

import java.util.Objects;

public interface BoolPredicate {
	/**
	 * Evaluates this predicate on the given argument.
	 *
	 * @param value the input argument
	 * @return {@code true} if the input argument matches the predicate,
	 * otherwise {@code false}
	 */
	boolean test(boolean value);

	/**
	 * Returns a composed predicate that represents a short-circuiting logical
	 * AND of this predicate and another.  When evaluating the composed
	 * predicate, if this predicate is {@code false}, then the {@code other}
	 * predicate is not evaluated.
	 *
	 * <p>Any exceptions thrown during evaluation of either predicate are relayed
	 * to the caller; if evaluation of this predicate throws an exception, the
	 * {@code other} predicate will not be evaluated.
	 *
	 * @param other a predicate that will be logically-ANDed with this
	 *              predicate
	 * @return a composed predicate that represents the short-circuiting logical
	 * AND of this predicate and the {@code other} predicate
	 * @throws NullPointerException if other is null
	 */
	default BoolPredicate and(BoolPredicate other) {
		Objects.requireNonNull(other);
		return (value) -> this.test(value) && other.test(value);
	}

	/**
	 * Returns a predicate that represents the logical negation of this
	 * predicate.
	 *
	 * @return a predicate that represents the logical negation of this
	 * predicate
	 */
	default BoolPredicate negate() {
		return (value) -> !this.test(value);
	}

	/**
	 * Returns a composed predicate that represents a short-circuiting logical
	 * OR of this predicate and another.  When evaluating the composed
	 * predicate, if this predicate is {@code true}, then the {@code other}
	 * predicate is not evaluated.
	 *
	 * <p>Any exceptions thrown during evaluation of either predicate are relayed
	 * to the caller; if evaluation of this predicate throws an exception, the
	 * {@code other} predicate will not be evaluated.
	 *
	 * @param other a predicate that will be logically-ORed with this
	 *              predicate
	 * @return a composed predicate that represents the short-circuiting logical
	 * OR of this predicate and the {@code other} predicate
	 * @throws NullPointerException if other is null
	 */
	default BoolPredicate or(BoolPredicate other) {
		Objects.requireNonNull(other);
		return (value) -> this.test(value) || other.test(value);
	}
}

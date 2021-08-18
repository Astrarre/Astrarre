package io.github.astrarre.util.v0.api;

import java.util.Objects;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public final class Either<A, B> {
	private static final Either<?, ?> EMPTY_LEFT = new Either<>(null, true), EMPTY_RIGHT = new Either<>(null, true);
	private final Object obj;
	private final boolean a;

	private Either(Object obj, boolean a) {
		this.obj = obj;
		this.a = a;
	}

	public static <A, B> Either<A, B> eitherOr(boolean val, A a, B b) {
		if(val) {
			return a(a);
		} else {
			return b(b);
		}
	}

	public static <A, B> Either<A, B> a(A val) {
		if(val == null) {
			return (Either<A, B>) EMPTY_LEFT;
		} else {
			return new Either<>(val, true);
		}
	}

	public static <A, B> Either<A, B> b(B val) {
		if(val == null) {
			return (Either<A, B>) EMPTY_RIGHT;
		} else {
			return new Either<>(val, false);
		}
	}

	/**
	 * Cast the non-present variable
	 *
	 * @return the same object
	 * @throws IllegalStateException if the left value is present
	 */
	public <C> Either<C, B> asA() {
		if(!this.a) {
			return (Either<C, B>) this;
		} else {
			throw new IllegalStateException("Left value is present!");
		}
	}

	/**
	 * Cast the non-present variable
	 *
	 * @return the same object
	 * @throws IllegalStateException if the left value is present
	 */
	public <C> Either<A, C> asB() {
		if(this.a) {
			return (Either<A, C>) this;
		} else {
			throw new IllegalStateException("Left value is present!");
		}
	}

	@SuppressWarnings("unchecked")
	public <C> Either<A, C> castB() {
		return (Either<A, C>) this;
	}

	@SuppressWarnings("unchecked")
	public <C> Either<C, B> castA() {
		return (Either<C, B>) this;
	}

	public <C> Either<C, B> mapA(Function<A, C> function) {
		if(this.a) {
			return a(function.apply((A) this.obj));
		} else {
			return (Either<C, B>) this;
		}
	}

	public <C> Either<A, C> mapB(Function<A, C> function) {
		if(this.a) {
			return (Either<A, C>) this;
		} else {
			return b(function.apply((A) this.obj));
		}
	}

	@Nullable
	public A getA() {
		if(this.a) {
			return (A) this.obj;
		} else {
			return null;
		}
	}

	@Nullable
	public B getB() {
		if(this.a) {
			return null;
		} else {
			return (B) this.obj;
		}
	}

	public boolean hasA() {
		return !this.a;
	}

	public boolean hasB() {
		return this.a;
	}

	@Override
	public int hashCode() {
		int result = this.obj != null ? this.obj.hashCode() : 0;
		result = 31 * result + (this.a ? 1 : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		return o instanceof Either<?, ?> either && this.a == either.a && Objects.equals(this.obj, either.obj);
	}

	@Override
	public String toString() {
		if(this.a) {
			return "[" + this.obj + ", ()]";
		} else {
			return "[(), " + this.obj + "]";
		}
	}
}
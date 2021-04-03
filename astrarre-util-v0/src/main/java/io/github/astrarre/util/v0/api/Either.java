package io.github.astrarre.util.v0.api;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

public final class Either<A, B> {
	private final Object obj;
	private final boolean side;

	private Either(Object obj, boolean side) {
		this.obj = obj;
		this.side = side;
	}

	public static <A, B> Either<A, B> eitherOr(boolean val, A a, B b) {
		if(val) {
			return ofLeft(a);
		} else {
			return ofRight(b);
		}
	}

	public static <A, B> Either<A, B> ofLeft(A val) {
		return new Either<>(val, true);
	}

	public static <A, B> Either<A, B> ofRight(B val) {
		return new Either<>(val, false);
	}

	/**
	 * @return the same object
	 * @throws IllegalStateException if the left value is present
	 */
	public <C> Either<C, B> asLeft() {
		if (!this.side) {
			return (Either<C, B>) this;
		} else {
			throw new IllegalStateException("Left value is present!");
		}
	}

	public <C> Either<A, C> asRight() {
		if (this.side) {
			return (Either<A, C>) this;
		} else {
			throw new IllegalStateException("Left value is present!");
		}
	}

	@Nullable
	public A getLeft() {
		if (this.side) {
			return (A) this.obj;
		} else {
			return null;
		}
	}

	@Nullable
	public B getRight() {
		if (this.side) {
			return null;
		} else {
			return (B) this.obj;
		}
	}

	public boolean hasRight() {
		return !this.side;
	}

	public boolean hasLeft() {
		return this.side;
	}

	@Override
	public int hashCode() {
		int result = this.obj != null ? this.obj.hashCode() : 0;
		result = 31 * result + (this.side ? 1 : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Either)) {
			return false;
		}

		Either<?, ?> either = (Either<?, ?>) o;

		if (this.side != either.side) {
			return false;
		}
		return Objects.equals(this.obj, either.obj);
	}

	@Override
	public String toString() {
		if (this.side) {
			return "[" + this.obj + ", null]";
		} else {
			return "[null, " + this.obj + "]";
		}
	}
}
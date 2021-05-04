package io.github.astrarre.util.v0.api;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.astrarre.util.internal.mixin.LazyAccess;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A superior Lazy class to Mojang's, works with null values
 * @see net.minecraft.util.Lazy
 */
public final class Lazy<T> {
	@Nullable
	private Supplier<T> supplier;
	private T instance;

	public Lazy(T instance) {
		this.supplier = null;
		this.instance = instance;
	}

	public Lazy(@NotNull Supplier<T> supplier) {
		this.supplier = Objects.requireNonNull(supplier, "Supplier may not be null");
	}

	public static <T> Lazy<T> of(Supplier<T> supplier) {
		return new Lazy<>(supplier);
	}

	public static <T> Lazy<T> of(T value) {
		return new Lazy<>(value);
	}

	public static <T> Lazy<T> or(@Nullable T value, Supplier<T> getter) {
		if(value == null) {
			return new Lazy<>(getter);
		} else {
			return new Lazy<>(value);
		}
	}

	public T get() {
		T instance = this.instance;
		Supplier<T> supplier = this.supplier;
		if(supplier != null) {
			instance = Objects.requireNonNull(supplier.get(), "Lazy supplier may not return null!");
			this.instance = instance;
			this.supplier = null;
		}
		return instance;
	}

	/**
	 * Returns the instance returned by the supplier if the Lazy has already been evaluated. Returns null otherwise
	 */
	@Nullable
	public T getRaw() {
		return this.instance;
	}

	public Optional<T> raw() {
		return Optional.ofNullable(this.instance);
	}

	@Contract("_ -> new")
	public <K> Lazy<K> map(Function<T, K> mapper) {
		T instance = this.instance;
		if(this.supplier != null) {
			return new Lazy<>(() -> mapper.apply(this.get()));
		} else {
			return new Lazy<>(() -> mapper.apply(instance));
		}
	}

	/**
	 * @return true if the supplier for this Lazy has been called
	 */
	public boolean hasEvaluated() {
		return this.supplier == null;
	}

	public State getState() {
		if(this.supplier != null) {
			return State.UNEVALUATED;
		} else if(this.instance != null) {
			return State.PRESENT;
		} else {
			return State.NULL;
		}
	}

	@Contract("-> new")
	public net.minecraft.util.Lazy<T> toMC() {
		if(this.supplier == null) {
			net.minecraft.util.Lazy<T> lazy = new net.minecraft.util.Lazy<>(null);
			((LazyAccess)lazy).setValue(this.instance);
			return lazy;
		} else {
			return new net.minecraft.util.Lazy<>(this.supplier);
		}
	}

	public enum State {
		/**
		 * the lazy contains a non-null value
		 */
		PRESENT,
		/**
		 * the lazy was evaluated, and contains a null value
		 */
		NULL,
		/**
		 * the lazy has not been evaluated
		 */
		UNEVALUATED;
	}
}

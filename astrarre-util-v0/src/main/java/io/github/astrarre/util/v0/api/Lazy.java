package io.github.astrarre.util.v0.api;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A superior Lazy class to mojang's
 */
public final class Lazy<T> {
	private Supplier<T> supplier;
	private T instance;

	public Lazy(@NotNull T instance) {
		this.supplier = null;
		this.instance = Objects.requireNonNull(instance, "Lazy cannot be initialized with null value!");
	}

	public Lazy(Supplier<@NotNull T> supplier) {
		this.supplier = supplier;
	}

	public static <T> Lazy<T> of(Supplier<@NotNull T> supplier) {
		return new Lazy<>(supplier);
	}

	public static <T> Lazy<T> of(@NotNull T value) {
		return new Lazy<>(value);
	}

	public static <T> Lazy<T> or(@Nullable T value, Supplier<@NotNull T> getter) {
		if(value == null) {
			return new Lazy<>(getter);
		} else {
			return new Lazy<>(value);
		}
	}

	public T get() {
		T instance = this.instance;
		if(instance == null) {
			instance = Objects.requireNonNull(this.supplier.get(), "Lazy supplier may not return null!");
			this.instance = instance;
			this.supplier = null;
		}
		return instance;
	}

	@Nullable
	public T getRaw() {
		return this.instance;
	}

	public Optional<T> raw() {
		return Optional.ofNullable(this.instance);
	}

	public <K> Lazy<K> map(Function<T, K> mapper) {
		T instance = this.instance;
		if(instance == null) {
			return new Lazy<>(() -> mapper.apply(this.get()));
		} else {
			return new Lazy<>(() -> mapper.apply(instance));
		}
	}
}

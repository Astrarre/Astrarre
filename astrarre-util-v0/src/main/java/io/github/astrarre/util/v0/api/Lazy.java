package io.github.astrarre.util.v0.api;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A superior Lazy class to Mojang's, works with null values
 *
 * @see net.minecraft.util.Lazy
 */
public final class Lazy<T> implements Supplier<T> {
	public static final Lazy<?> EMPTY = new Lazy<>((Object) null);

	@Nullable private Supplier<T> supplier;
	private T instance;

	/**
	 * @see #of(Object)
	 */
	public Lazy(T instance) {
		this.supplier = null;
		this.instance = instance;
	}

	public Lazy(@NotNull Supplier<T> supplier) {
		this.supplier = Objects.requireNonNull(supplier, "Supplier may not be null");
	}

	public static Lazy<Void> init(Runnable runnable) {
		return of(() -> {
			runnable.run();
			return null;
		});
	}

	public static <T> Lazy<T> of(Supplier<T> supplier) {
		return new Lazy<>(supplier);
	}

	public static <T> Lazy<T> of(T value) {
		if(value == null) {
			return empty();
		}
		return new Lazy<>(value);
	}

	/**
	 * @return a pre-evaluated lazy for `null`
	 */
	public static <T> Lazy<T> empty() {
		return (Lazy<T>) EMPTY;
	}

	/**
	 * @return if `value` is null, returns a new lazy with the given function, else uses the value for a pre-evaluated lazy
	 */
	public static <T> Lazy<T> or(@Nullable T value, Supplier<T> getter) {
		if(value == null) {
			return new Lazy<>(getter);
		} else {
			return of(value);
		}
	}

	/**
	 * If {@link #hasEvaluated()} the lazy returns the instance, else it evaluates the supplier and returns the instance.
	 */
	@Override
	public T get() {
		T instance = this.instance;
		Supplier<T> supplier = this.supplier;
		if(supplier != null) {
			this.instance = instance = supplier.get();
			this.supplier = null;
		}
		return instance;
	}

	/**
	 * @return a new lazy that will map, or maps the value of this lazy
	 */
	@Contract("_ -> new")
	public <K> Lazy<K> map(Function<T, K> mapper) {
		T instance = this.instance;
		if(this.supplier != null) {
			return new Lazy<>(() -> mapper.apply(this.get()));
		} else {
			return new Lazy<>(mapper.apply(instance));
		}
	}

	/**
	 * Returns the instance returned by the supplier if the Lazy has already been evaluated. Returns null otherwise
	 */
	@Nullable
	public T getRaw() {
		return this.instance;
	}

	/**
	 * @return if the Lazy is evaluated, return the instance, else evaluates the given supplier
	 */
	public T rawOrElseGet(Supplier<T> supplier) {
		if(this.supplier == null) {
			return this.instance;
		} else {
			return supplier.get();
		}
	}

	/**
	 * @return if the Lazy is evaluated, return the instance, else return val
	 */
	public T rawOrElse(T val) {
		if(this.supplier == null) {
			return this.instance;
		} else {
			return val;
		}
	}

	public T rawOrThrow() {
		if(this.supplier == null) {
			return this.instance;
		} else {
			throw new IllegalStateException("Lazy has not been evaluated!");
		}
	}

	public <E extends Throwable> T rawOrThrow(Supplier<E> exception) throws E {
		if(this.supplier != null) {
			throw exception.get();
		} else {
			return this.instance;
		}
	}

	public <E extends Throwable> T rawOrThrow(E exception) throws E {
		if(this.supplier != null) {
			throw exception;
		} else {
			return this.instance;
		}
	}

	/**
	 * @return true if the supplier for this Lazy has been called
	 */
	public boolean hasEvaluated() {
		return this.supplier == null;
	}

	/**
	 * if the lazy has been evaluated, performs the given action with the value
	 *
	 * @see #hasEvaluated()
	 */
	public boolean ifEvaluated(Consumer<T> consumer) {
		if(this.supplier == null) {
			consumer.accept(this.instance);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return evaluates the lazy and checks if the instance is not null
	 */
	public boolean isPresent() {
		return this.get() != null;
	}

	/**
	 * @return evaluates the lazy and checks if the instance is null
	 */
	public boolean isEmpty() {
		return this.get() == null;
	}

	/**
	 * After evaluation, if a value is present, performs the given action with the value, otherwise returns false.
	 *
	 * @see #isPresent()
	 */
	public boolean ifPresent(Consumer<T> consumer) {
		T val = this.get();
		if(val != null) {
			consumer.accept(val);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * After evaluation, if a value is present, performs the given action with the value, otherwise executes the other action.
	 *
	 * @see #isPresent()
	 */
	public boolean ifPresentOrElse(Consumer<T> consumer, Runnable runnable) {
		T val = this.get();
		if(val != null) {
			consumer.accept(val);
			return true;
		} else {
			runnable.run();
			return false;
		}
	}

	public T getOrThrow() {
		T val = this.get();
		if(val == null) {
			throw new NullPointerException("Lazy evaluated to null!");
		} else {
			return val;
		}
	}

	public <E extends Throwable> T getOrThrow(Supplier<E> exception) throws E {
		T val = this.get();
		if(val == null) {
			throw exception.get();
		} else {
			return val;
		}
	}

	public <E extends Throwable> T getOrThrow(E exception) throws E {
		T val = this.get();
		if(val == null) {
			throw exception;
		} else {
			return val;
		}
	}

	/**
	 * @return a new lazy with the current value, if not evaluated, it's null
	 */
	public Lazy<@Nullable T> raw() {
		return of(this.instance);
	}

	/**
	 * Evaluates the lazy, if the predicate is true, returns the current instance, else, returns {@link Lazy#EMPTY}
	 */
	public Lazy<T> filter(Predicate<T> predicate) {
		T val = this.get();
		if(predicate.test(val)) {
			return this;
		} else {
			return empty();
		}
	}

	public Stream<T> stream() {
		if(this.supplier == null) {
			return Stream.of(this.instance);
		} else {
			var iter = Stream.iterate((Object) this, t -> ((Lazy)t).supplier != null, t -> ((Lazy)t).get());
			return (Stream<T>) iter;
		}
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

	public Optional<T> rawOpt() {
		return Optional.ofNullable(this.instance);
	}

	@Contract("-> new")
	public net.minecraft.util.Lazy<T> toMC() {
		if(this.supplier == null) {
			return new net.minecraft.util.Lazy<>(() -> this.instance);
		} else {
			// has to be `this`/`this::get` instead of passing the supplier, because we don't want to evaluate the supplier twice on accident
			return new net.minecraft.util.Lazy<>(this);
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
		UNEVALUATED
	}

	@Override
	public String toString() {
		if(this.supplier != null) {
			return "Lazy[<unevaluated>]";
		} else {
			return "Lazy[" + this.instance + "]";
		}
	}
}

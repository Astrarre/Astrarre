package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.MapFilter;
import io.github.astrarre.access.v0.api.helper.AbstractAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Filter based on a registry entry's id
 */
public class RegistryAccessHelper<T, F> extends AbstractAccessHelper<T, F> {
	private final Registry<T> registry;
	private final MapFilter<Identifier, F> filter;

	/**
	 * creates a new function helper who's incoming type is not the same as the type being filtered
	 */
	public static <I, T, F> RegistryAccessHelper<T, F> create(Registry<T> registry, AbstractAccessHelper<I, F> copyFrom, Function<I, T> mapper) {
		return new RegistryAccessHelper<>(registry, copyFrom.iterFunc, function -> copyFrom.andThen.accept(i -> function.apply(mapper.apply(i))), copyFrom.empty);
	}

	public RegistryAccessHelper(Registry<T> registry, AbstractAccessHelper<T, F> copyFrom) {
		this(registry, copyFrom.iterFunc, copyFrom.andThen, copyFrom.empty);
	}

	public RegistryAccessHelper(Registry<T> registry, Access<F> func, Function<Function<T, F>, F> adder) {
		this(registry, func, adder, null);
	}

	public RegistryAccessHelper(Registry<T> registry, Access<F> func, Function<Function<T, F>, F> and, F empty) {
		this(registry, func.combiner, f -> func.andThen(and.apply(f)), empty);
	}

	public RegistryAccessHelper(Registry<T> registry, IterFunc<F> func, Consumer<Function<T, F>> adder, F empty) {
		super(func, adder, empty);
		this.registry = registry;
		this.filter = new MapFilter<>(func, empty);
	}

	public RegistryAccessHelper(Registry<T> registry, IterFunc<F> func, Consumer<Function<T, F>> adder) {
		this(registry, func, adder, null);
	}

	/**
	 * creates a new function helper who's incoming type is not the same as the type being filtered
	 */
	public static <I, T, F> RegistryAccessHelper<T, F> create(Registry<T> registry,
			IterFunc<F> func,
			Consumer<Function<I, F>> adder,
			Function<I, T> mapper) {
		return create(registry, func, adder, mapper, null);
	}

	/**
	 * creates a new function helper who's incoming type is not the same as the type being filtered
	 */
	public static <I, T, F> RegistryAccessHelper<T, F> create(Registry<T> registry,
			IterFunc<F> func,
			Consumer<Function<I, F>> adder,
			Function<I, T> mapper,
			F empty) {
		return new RegistryAccessHelper<>(registry, func, function -> adder.accept(i -> function.apply(mapper.apply(i))), empty);
	}

	public static <I, T, F> RegistryAccessHelper<T, F> create(Registry<T> registry,
			Access<F> func,
			Function<Function<I, F>, F> and,
			Function<I, T> mapper) {
		return create(registry, func, and, mapper, null);
	}

	public static <I, T, F> RegistryAccessHelper<T, F> create(Registry<T> registry,
			Access<F> func,
			Function<Function<I, F>, F> and,
			Function<I, T> mapper,
			F empty) {
		return new RegistryAccessHelper<>(registry, func, function -> and.apply(i -> function.apply(mapper.apply(i))), empty);
	}

	public RegistryAccessHelper<T, F> forId(Identifier registryId, F function) {
		if(this.filter.add(registryId, function)) {
			this.andThen.accept(t -> {
				Identifier id = this.registry.getId(t);
				return this.filter.get(id);
			});
		}
		return this;
	}
}

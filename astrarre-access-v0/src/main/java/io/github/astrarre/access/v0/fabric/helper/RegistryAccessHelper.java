package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.MapFilter;
import io.github.astrarre.access.v0.api.helper.AbstractAccessHelper;
import io.github.astrarre.access.v0.api.helper.AccessHelpers;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Filter based on a registry entry's id
 */
public class RegistryAccessHelper<T, F> extends AbstractAccessHelper<T, F> {
	private final Registry<T> registry;
	private final MapFilter<Identifier, F> filter;

	public RegistryAccessHelper(Registry<T> registry, AccessHelpers.Context<T, F> copyFrom) {
		super(copyFrom);
		this.registry = registry;
		this.filter = new MapFilter<>(copyFrom.func(), copyFrom.empty());
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

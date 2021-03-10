package io.github.astrarre.recipes.v0.api.value;

import io.github.astrarre.recipes.v0.api.util.Either;
import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public class RegistryValueParser<T> implements ValueParser<T> {
	protected final Registry<T> registry;
	private final Id air;
	private final T defaultValue;

	public RegistryValueParser(Registry<T> registry) {
		this.registry = registry;
		if(registry instanceof DefaultedRegistry) {
			this.air = Id.of(((DefaultedRegistry<T>) registry).getDefaultId());
			this.defaultValue = registry.get(this.air.to());
		} else {
			this.air = null;
			this.defaultValue = null;
		}
	}

	@Override
	public Either<T, String> parse(PeekableReader reader) {
		Either<Id, String> either = ValueParser.ID.parse(reader);
		if(either.hasLeft()) {
			Id id = either.getLeft();
			T value = this.registry.get(id.to());
			if(value == this.defaultValue && !id.equals(this.air)) {
				return Either.ofRight("item not found " + id);
			}
			return Either.ofLeft(value);
		}
		return Either.ofRight("no item id");
	}
}

package io.github.astrarre.recipes.v0.api.ingredient;

import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.value.ValueParser;

public class RequirePart<V extends Comparable<V>> implements RecipePart<V, V> {
	protected final ValueParser<V> parser;
	protected RequirePart(ValueParser<V> parser) {
		this.parser = parser;
	}

	@Override
	public ValueParser<V> parser() {
		return this.parser;
	}

	@Override
	public boolean test(V inp, V val) {
		return inp.compareTo(val) >= 0;
	}

	@Override
	public void apply(V inp, V val) {
	}
}

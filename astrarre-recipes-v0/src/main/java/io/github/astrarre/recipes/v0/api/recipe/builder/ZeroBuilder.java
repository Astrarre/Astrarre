package io.github.astrarre.recipes.v0.api.recipe.builder;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.recipes.v0.api.RecipePart;

public class ZeroBuilder extends BaseBuilder<ZeroBuilder> {
	public static ZeroBuilder builder() {
		return new ZeroBuilder();
	}

	ZeroBuilder() {
		super(new ArrayList<>());
	}

	ZeroBuilder(List<?> list) {
		super(list);
	}

	@Override
	protected ZeroBuilder copy(List<?> objects) {
		return new ZeroBuilder(objects);
	}

	public <A> MonoBuilder<A> add(RecipePart<?, A> part) {
		List<Object> objects = new ArrayList<>(this.list);
		objects.add(part);
		return new MonoBuilder<>(objects);
	}
}

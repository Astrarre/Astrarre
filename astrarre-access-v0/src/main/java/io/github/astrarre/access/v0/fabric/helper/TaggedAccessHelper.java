package io.github.astrarre.access.v0.fabric.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.AbstractAccessHelper;
import io.github.astrarre.access.v0.api.helper.AccessHelpers;
import io.github.astrarre.access.v0.api.util.FunctionCompiler;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.tag.Tag;

public class TaggedAccessHelper<T, F> extends AbstractAccessHelper<T, F> {
	protected final Map<Tag<T>, FunctionCompiler<F>> functions = new HashMap<>();
	protected final Function<Tag<T>, FunctionCompiler<F>> init = this::getCompiler;

	public TaggedAccessHelper(AccessHelpers.Context<T, F> copyFrom) {
		super(copyFrom);
	}

	public TaggedAccessHelper<T, F> forTag(Tag<T> tag, F function) {
		this.functions.computeIfAbsent(tag, this.init).add(function);
		return this;
	}

	private FunctionCompiler<F> getCompiler(Tag<T> a) {
		FunctionCompiler<F> compiler = new FunctionCompiler<>(this.iterFunc, this.empty);
		this.andThen.accept(i -> {
			if (a.contains(i)) {
				return compiler.get();
			} else {
				return null;
			}
		});
		return compiler;
	}
}

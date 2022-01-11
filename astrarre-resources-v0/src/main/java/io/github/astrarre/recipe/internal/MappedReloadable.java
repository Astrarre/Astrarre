package io.github.astrarre.recipe.internal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import io.github.astrarre.recipe.v0.api.Reloadable;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;

public class MappedReloadable<T, V> extends AbstractReloadable<V> {
	final Reloadable<T> input;
	final Function<T, V> mappingFunction;
	final Reloadable<?>[] dependencies;
	boolean setDefault;
	int visitedDependencies;

	public MappedReloadable(Reloadable<T> input, Function<T, V> function, Reloadable<?>[] dependencies) {
		this.input = input;
		this.mappingFunction = function;
		this.dependencies = dependencies;
		input.afterUpdate(this::attemptPopulate);
		for(Reloadable<?> dependency : dependencies) {
			dependency.afterUpdate(this::attemptPopulate);
		}
	}

	void attemptPopulate(Object ignore) {
		if(++this.visitedDependencies >= this.dependencies.length + 1) {
			V apply = this.mappingFunction.apply(this.input.get());
			this.value = apply;
			this.runListeners(apply);
		}
	}

	@Override
	public AbstractReloadable<V> setDefault(V value) {
		this.setDefault = true;
		return super.setDefault(value);
	}

	@Override
	protected V getDefaultValue() {
		if(!this.setDefault && this.input instanceof AbstractReloadable a) {
			return this.mappingFunction.apply((T) a.defaultValue);
		}
		return super.getDefaultValue();
	}

	@Override
	protected CompletableFuture<V> reload(
			ResourceReloader.Synchronizer synchronizer,
			ResourceManager manager,
			Profiler prepareProfiler,
			Profiler applyProfiler,
			Executor prepareExecutor,
			Executor applyExecutor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() {
		this.visitedDependencies = 0;
	}
}

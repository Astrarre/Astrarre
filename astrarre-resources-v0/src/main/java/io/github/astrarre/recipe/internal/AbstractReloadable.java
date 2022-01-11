package io.github.astrarre.recipe.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import io.github.astrarre.recipe.v0.api.Reloadable;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;

public abstract class AbstractReloadable<V> implements Reloadable<V> {
	final WeakHashMap<Object, Consumer<V>> postMap = new WeakHashMap<>();
	final List<Consumer<V>> post = new ArrayList<>();
	V value, defaultValue;

	public void addTo(
			List<CompletableFuture<?>> futures,
			ResourceReloader.Synchronizer synchronizer,
			ResourceManager manager,
			Profiler prepareProfiler,
			Profiler applyProfiler,
			Executor prepareExecutor,
			Executor applyExecutor) {
		futures.add(this.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor).thenAccept(this::runListeners));
	}

	protected void runListeners(V v) {
		this.value = v;
		for(Consumer<V> consumer : this.postMap.values()) {
			consumer.accept(v);
		}
		for(Consumer<V> consumer : this.post) {
			consumer.accept(v);
		}
	}

	@Override
	public AbstractReloadable<V> afterUpdate(Object anchor, Consumer<V> listener) {
		this.postMap.put(anchor, listener);
		return this;
	}

	@Override
	public AbstractReloadable<V> afterUpdate(Consumer<V> listener) {
		this.post.add(listener);
		return this;
	}

	@Override
	public AbstractReloadable<V> setDefault(V value) {
		this.defaultValue = value;
		return this;
	}

	@Override
	public V get() {
		V val = this.value;
		if(val == null) {
			return this.getDefaultValue();
		} else {
			return val;
		}
	}

	protected V getDefaultValue() {
		return this.defaultValue;
	}

	public void reset() {
		this.value = null;
	}

	public boolean doesResourceLoad() {
		return false;
	}

	protected abstract CompletableFuture<V> reload(
			ResourceReloader.Synchronizer synchronizer,
			ResourceManager manager,
			Profiler prepareProfiler,
			Profiler applyProfiler,
			Executor prepareExecutor,
			Executor applyExecutor);
}

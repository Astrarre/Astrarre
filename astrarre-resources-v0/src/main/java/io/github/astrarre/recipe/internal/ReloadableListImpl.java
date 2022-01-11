package io.github.astrarre.recipe.internal;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;

public class ReloadableListImpl<T> extends AbstractReloadable<List<T>> {
	public void setValue(List<T> value) {
		this.value = value;
		this.runListeners(value);
	}

	@Override
	protected CompletableFuture<List<T>> reload(
			ResourceReloader.Synchronizer synchronizer,
			ResourceManager manager,
			Profiler prepareProfiler,
			Profiler applyProfiler,
			Executor prepareExecutor,
			Executor applyExecutor) {
		throw new UnsupportedOperationException();
	}
}

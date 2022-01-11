package io.github.astrarre.recipe.internal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;

public class EmptyManager extends AbstractReloadable<Void> {
	private static final CompletableFuture<Void> COMPLETED = CompletableFuture.completedFuture(null);

	public EmptyManager() {}

	@Override
	protected CompletableFuture<Void> reload(
			ResourceReloader.Synchronizer synchronizer,
			ResourceManager manager,
			Profiler prepareProfiler,
			Profiler applyProfiler,
			Executor prepareExecutor,
			Executor applyExecutor) {
		return COMPLETED;
	}
}

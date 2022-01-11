package io.github.astrarre.recipe.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ReloadableManager implements ResourceReloader {
	public static final ReloadableManager SERVER = new ReloadableManager();

	@Environment(EnvType.CLIENT)
	public static class ClientHolder {
		public static final ReloadableManager CLIENT = new ReloadableManager();
	}

	public final Set<AbstractReloadable<?>> load = Collections.newSetFromMap(new WeakHashMap<>());

	@Override
	public CompletableFuture<Void> reload(
			Synchronizer synchronizer,
			ResourceManager manager,
			Profiler prepareProfiler,
			Profiler applyProfiler,
			Executor prepareExecutor,
			Executor applyExecutor) {
		List<CompletableFuture<?>> futures = new ArrayList<>();
		for(AbstractReloadable<?> reloadable : this.load) {
			reloadable.addTo(futures, synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
		}
		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenCompose(synchronizer::whenPrepared);
	}

	public void invalidate() {
		for(AbstractReloadable<?> reloadable : this.load) {
			reloadable.reset();
		}
	}

	@Override
	public String getName() {
		return "astrarre-resource-v0 api loader";
	}
}

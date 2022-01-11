package io.github.astrarre.recipe.internal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.gson.Gson;
import io.github.astrarre.recipe.v0.api.ResourceIdentifiable;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class SingleLoadingReloadable<V> extends AbstractReloadable<V> {
	final Gson gson;
	final Type type;
	final Identifier path;

	public SingleLoadingReloadable(Gson gson, Type type, Identifier path) {
		this.gson = gson;
		this.type = type;
		this.path = path;
	}

	@Override
	protected CompletableFuture<V> reload(
			ResourceReloader.Synchronizer synchronizer,
			ResourceManager manager,
			Profiler prepareProfiler,
			Profiler applyProfiler,
			Executor prepareExecutor,
			Executor applyExecutor) {
		return CompletableFuture.supplyAsync(() -> {
			try(Resource resource = manager.getResource(this.path); Reader reader = new InputStreamReader(resource.getInputStream())) {
				return this.gson.fromJson(reader, this.type);
			} catch(IOException e) {
				throw Validate.rethrow(e);
			}
		}, prepareExecutor).thenCompose(synchronizer::whenPrepared).thenApplyAsync(o -> {
			if(o instanceof ResourceIdentifiable r) {
				r.setResourceId(this.path);
			}
			return (V) o;
		}, applyExecutor);
	}

	@Override
	public boolean doesResourceLoad() {
		return true;
	}
}

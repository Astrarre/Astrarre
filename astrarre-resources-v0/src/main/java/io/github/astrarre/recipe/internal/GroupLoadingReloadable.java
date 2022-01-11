package io.github.astrarre.recipe.internal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import com.google.gson.Gson;
import io.github.astrarre.recipe.v0.api.ResourceIdentifiable;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class GroupLoadingReloadable<V, C extends Collection<V>> extends AbstractReloadable<C> {
	final Gson gson;
	final Type type;
	final IntFunction<C> collection;
	final String startingPath;
	final Predicate<String> pathPredicate;

	public GroupLoadingReloadable(Gson gson, Type type, IntFunction<C> collection, String path, Predicate<String> predicate) {
		this.gson = gson;
		this.type = type;
		this.collection = collection;
		this.startingPath = path;
		this.pathPredicate = predicate;
	}

	@Override
	protected CompletableFuture<C> reload(
			ResourceReloader.Synchronizer synchronizer,
			ResourceManager manager,
			Profiler prepareProfiler,
			Profiler applyProfiler,
			Executor prepareExecutor,
			Executor applyExecutor) {
		record ValuePair<V>(Identifier id, V value) {}
		List<CompletableFuture<V>> futures = new ArrayList<>();
		List<ValuePair<V>> values = new Vector<>();
		for(Identifier resourceId : manager.findResources(this.startingPath, this.pathPredicate)) {
			futures.add(CompletableFuture.supplyAsync(() -> {
				try(Resource resource = manager.getResource(resourceId); Reader reader = new InputStreamReader(resource.getInputStream())) {
					V vs = this.gson.fromJson(reader, this.type);
					values.add(new ValuePair<>(resourceId, vs));
					return vs;
				} catch(IOException e) {
					throw Validate.rethrow(e);
				}
			}, prepareExecutor));
		}
		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenCompose(synchronizer::whenPrepared).thenApplyAsync(unused -> {
			C copy = this.collection.apply(values.size());
			for(ValuePair<V> pair : values) {
				if(pair.value instanceof ResourceIdentifiable i) {
					i.setResourceId(pair.id);
					copy.add(pair.value);
				}
			}
			return copy;
		}, applyExecutor);
	}

	@Override
	public boolean doesResourceLoad() {
		return true;
	}
}

package io.github.astrarre.recipe.v0.api;

import static io.github.astrarre.recipe.internal.ReloadableInternals.delegateType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.github.astrarre.recipe.internal.AbstractReloadable;
import io.github.astrarre.recipe.internal.EmptyManager;
import io.github.astrarre.recipe.internal.GroupLoadingReloadable;
import io.github.astrarre.recipe.internal.MappedReloadable;
import io.github.astrarre.recipe.internal.ReloadableManager;
import io.github.astrarre.recipe.internal.SingleLoadingReloadable;

import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

@SuppressWarnings({
		"unchecked",
		"UnstableApiUsage"
})
public interface Reloadable<V> {
	Reloadable<Void> SERVER_RESOURCES = new EmptyManager();
	Reloadable<Void> CLIENT_RESOURCES = new EmptyManager();

	static <T> ReloadableList<T> group(String path, Predicate<String> predicate, T... hack) {
		return (ReloadableList<T>) group(hack.getClass().componentType(), path, predicate);
	}

	static <T> ReloadableList<T> group(Gson gson, String path, Predicate<String> predicate, T... hack) {
		return (ReloadableList<T>) group(gson, hack.getClass().componentType(), path, predicate);
	}

	static <T> ReloadableList<T> group(Class<T> token, String path, Predicate<String> predicate) {
		return group(Recipes.DEFAULT, token, path, predicate);
	}

	static <T> ReloadableList<T> group(Gson gson, Class<T> token, String path, Predicate<String> predicate) {
		return groupInternal(gson, token, path, predicate);
	}

	static <T> ReloadableList<T> group(TypeToken<T> token, String path, Predicate<String> predicate) {
		return group(Recipes.DEFAULT, token, path, predicate);
	}

	static <T> ReloadableList<T> group(Gson gson, TypeToken<T> token, String path, Predicate<String> predicate) {
		return groupInternal(gson, token.getType(), path, predicate);
	}
	
	// singles

	static <T> Reloadable<T> single(Identifier path, T... hack) {
		return (Reloadable<T>) single(hack.getClass().componentType(), path);
	}

	static <T> Reloadable<T> single(Gson gson, Identifier path, T... hack) {
		return (Reloadable<T>) single(gson, hack.getClass().componentType(), path);
	}

	static <T> Reloadable<T> single(Class<T> token, Identifier path) {
		return single(Recipes.DEFAULT, token, path);
	}

	static <T> Reloadable<T> single(Gson gson, Class<T> token, Identifier path) {
		return singleInternal(gson, token, path);
	}

	static <T> Reloadable<T> single(TypeToken<T> token, Identifier path) {
		return single(Recipes.DEFAULT, token, path);
	}

	static <T> Reloadable<T> single(Gson gson, TypeToken<T> token, Identifier path) {
		return singleInternal(gson, token.getType(), path);
	}
	
	private static <T> Reloadable<T> singleInternal(Gson gson, Type type, Identifier path) {
		SingleLoadingReloadable<T> instance = new SingleLoadingReloadable<>(gson, type, path);
		ReloadableManager.SERVER.load.add(instance);
		return instance;
	}

	private static <T> ReloadableList<T> groupInternal(Gson gson, Type token, String path, Predicate<String> predicate) {
		var instance = new GroupLoadingReloadable<>(gson, token, ArrayList::new, path, predicate);
		ReloadableManager.SERVER.load.add(instance);
		return delegateType(instance, List.class);
	}

	default Reloadable<V> markClient() {
		if(this instanceof AbstractReloadable<?> a && a.doesResourceLoad()) {
			ReloadableManager.SERVER.load.remove(a);
			if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				ReloadableManager.ClientHolder.CLIENT.load.add(a);
			}
		} else {
			throw new UnsupportedOperationException("Cannot mark non-root reloadable as client!");
		}
		return this;
	}

	/**
	 * The order this is executed in is undefined, however all anchored listeners are executed after all unanchored ones {@link
	 * #afterUpdate(Consumer)}
	 *
	 * @param anchor when this is garbage collected, the listener is removed
	 * @param listener fired before the json is parsed
	 */
	Reloadable<V> afterUpdate(Object anchor, Consumer<V> listener);

	Reloadable<V> afterUpdate(Consumer<V> listener);

	default <N> Reloadable<N> map(Function<V, N> mappingFunction, Reloadable<?>... dependencies) {
		return new MappedReloadable<>(this, mappingFunction, dependencies);
	}

	Reloadable<V> setDefault(V value);

	V get();

	Object __INIT__ = Util.make(() -> {
		ReloadableManager.SERVER.load.add((AbstractReloadable<?>) SERVER_RESOURCES);
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ReloadableManager.ClientHolder.CLIENT.load.add((AbstractReloadable<?>) CLIENT_RESOURCES);
		}
		return null;
	});
}

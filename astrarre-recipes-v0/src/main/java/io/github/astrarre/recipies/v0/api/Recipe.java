package io.github.astrarre.recipies.v0.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import com.google.common.collect.Iterables;
import io.github.astrarre.recipies.v0.api.ingredient.RecipeComponentParser;
import io.github.astrarre.recipies.v0.api.io.CharInput;
import io.github.astrarre.util.v0.api.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;

public class Recipe {
	private static final Logger LOGGER = LogManager.getLogger("RecipeFactory");
	public static <T, I> Predicate<T> getInput(RecipeComponentParser<I, T> provider, String name) {
		AtomicReference<List<I>> input = new AtomicReference<>();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleResourceReloadListener<List<I>>() {
			@Override
			public Identifier getFabricId() {
				return new Identifier("mcrf", name);
			}

			@Override
			public CompletableFuture<List<I>> load(ResourceManager manager, Profiler profiler, Executor executor) {
				return CompletableFuture.supplyAsync(() -> {
					List<I> inputs = new ArrayList<>();
					for (Identifier resource : manager.findResources(name, p -> p.endsWith(".mcrf"))) {
						try {
							Resource res = manager.getResource(resource);
							LOGGER.info("Loading: " + res + " in " + res.getResourcePackName());
							InputStream stream = res.getInputStream();
							InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
							CharInput input = new CharInput(new BufferedReader(reader));
							char[] buffer = BUFFER.get();
							while (skipWhitespace(reader, buffer) != -1) {
								inputs.add(provider.parse(input));
							}
						} catch (IOException e) {
							throw Validate.rethrow(e);
						}
					}
					return inputs;
				});
			}

			@Override
			public CompletableFuture<Void> apply(List<I> o, ResourceManager manager, Profiler profiler, Executor executor) {
				input.set(o);
				return CompletableFuture.completedFuture(null);
			}
		});
		return t -> {
			List<I> inputs = input.get();
			return Iterables.any(inputs, i -> provider.apply(i, t));
		};
	}

	public static final ThreadLocal<char[]> BUFFER = ThreadLocal.withInitial(() -> new char[6]);
	public static int skipWhitespace(Reader reader, char[] buffer) {
		try {
			reader.mark(buffer.length);
			int chars = reader.read(buffer);
			for (int i = 0; i < chars; i++) {
				char c = buffer[i];
				if(!Character.isWhitespace(c)) {
					reader.reset();
					if(reader.read(buffer, 0, i) != i) {
						throw new IOException("Invalid mark/reset!");
					}
					return i;
				}
			}
			return chars;
		} catch (IOException e) {
			throw Validate.rethrow(e);
		}
	}
}

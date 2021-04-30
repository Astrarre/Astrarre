package io.github.astrarre.recipe.v0.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.common.collect.ForwardingList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.github.astrarre.recipe.internal.mixin.FluidTagsAccess;
import io.github.astrarre.recipe.internal.serializer.IngredientSerializer;
import io.github.astrarre.recipe.internal.serializer.ItemStackSerializer;
import io.github.astrarre.recipe.internal.serializer.TagSerializer;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

/**
 * @see Inventories
 */
public class Recipes {
	public static final Gson DEFAULT = defaultGson().create();

	/**
	 * creates a new recipe instance with the default gson instance
	 */
	public static <T extends Recipe> List<T> createRecipe(Identifier recipeId, Class<T> type) {
		return createRecipe(DEFAULT, recipeId, type);
	}

	/**
	 * recipes are in json at recipes/< namespace >/< path >/...
	 * @param type the type of your recipe class
	 * @return a list of recipe instances (it's autoupdated on recipe change)
	 */
	public static <T extends Recipe> List<T> createRecipe(Gson gson, Identifier recipeId, Class<T> type) {
		List<T>[] ref = new List[]{new Vector<>()};
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleResourceReloadListener<List<JsonObject>>() {
			@Override
			public CompletableFuture<List<JsonObject>> load(ResourceManager manager, Profiler profiler, Executor executor) {
				return CompletableFuture.supplyAsync(() -> {
					List<JsonObject> objects = new ArrayList<>();
					for (Identifier recipes : manager.findResources("recipes/" + recipeId.getNamespace() + "/" + recipeId.getPath(), s -> s.endsWith(".json"))) {
						try {
							Resource resource = manager.getResource(recipes);
							objects.add(gson.fromJson(new InputStreamReader(resource.getInputStream()), JsonObject.class));
						} catch (IOException e) {
							Validate.rethrow(e);
						}
					}
					return objects;
				});
			}

			@Override
			public CompletableFuture<Void> apply(List<JsonObject> data, ResourceManager manager, Profiler profiler, Executor executor) {
				return CompletableFuture.runAsync(() -> {
					ref[0].clear();
					for (JsonObject datum : data) {
						T instance = gson.fromJson(datum, type);
						ref[0].add(instance);
						instance.onInit();
					}
				});
			}

			@Override
			public Identifier getFabricId() {
				return new Identifier(recipeId.getNamespace(), "recipehandler/" + recipeId.getPath());
			}
		});
		return new ForwardingList<T>() {
			@Override
			protected List<T> delegate() {
				return ref[0];
			}
		};
	}

	/**
	 * New type adapters may be added at any time which may or may not break backwards compatibility with existing recipes
	 */
	public static GsonBuilder defaultGson() {
		return new GsonBuilder()
				       .registerTypeAdapter(ItemStack.class, ItemStackSerializer.INSTANCE)
				       .registerTypeAdapter(new TypeToken<Tag<Item>>() {}.getType(), new TagSerializer<>(ServerTagManagerHolder.getTagManager()::getItems))
				       .registerTypeAdapter(new TypeToken<Tag<Fluid>>() {}.getType(), new TagSerializer<>(ServerTagManagerHolder.getTagManager()::getFluids))
				       .registerTypeAdapter(new TypeToken<Tag<Block>>() {}.getType(), new TagSerializer<>(ServerTagManagerHolder.getTagManager()::getBlocks))
				       .registerTypeAdapter(new TypeToken<Tag<EntityType<?>>>() {}.getType(), new TagSerializer<>(ServerTagManagerHolder.getTagManager()::getEntityTypes))
				       .registerTypeAdapter(Ingredient.class, IngredientSerializer.INSTANCE);
	}
}

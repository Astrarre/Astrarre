package io.github.astrarre.recipe.v0.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ForwardingList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.astrarre.recipe.internal.serializer.IngredientSerializer;
import io.github.astrarre.recipe.internal.serializer.ItemStackSerializer;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

/**
 * @see Inventories
 */
public class Recipes {
	public static final Gson DEFAULT = defaultGson().create();

	/**
	 * creates a new recipe instance with the default gson instance
	 */
	public static <T> List<T> createRecipe(Identifier recipeId, Class<T> type) {
		return createRecipe(DEFAULT, recipeId, type);
	}

	/**
	 * recipes are in json at recipes/< namespace >/< path >/...
	 * @param type the type of your recipe class
	 * @return a list of recipe instances (it's autoupdated on recipe change)
	 */
	public static <T> List<T> createRecipe(Gson gson, Identifier recipeId, Class<T> type) {
		List<T>[] ref = new List[]{new ArrayList<>()};
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(recipeId.getNamespace(), "recipe/" + recipeId.getPath());
			}

			@Override
			public void apply(ResourceManager manager) {
				List<T> list = ref[0];
				list.clear();
				for (Identifier recipes : manager.findResources("recipes/" + recipeId.getNamespace() + "/" + recipeId.getPath(), s -> s.endsWith(".json"))) {
					try {
						Resource resource = manager.getResource(recipes);
						list.add(gson.fromJson(new InputStreamReader(resource.getInputStream()), type));
					} catch (IOException e) {
						Validate.rethrow(e);
					}
				}
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
				       .registerTypeAdapter(Ingredient.class, IngredientSerializer.INSTANCE);
	}
}

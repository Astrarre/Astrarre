package io.github.astrarre.recipe.v0.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ForwardingList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.astrarre.recipe.internal.serializer.IngredientSerializer;
import io.github.astrarre.recipe.internal.serializer.ItemStackSerializer;
import io.github.astrarre.recipe.internal.serializer.TagSerializer;
import io.github.astrarre.recipe.internal.vanilla.CustomRecipeSerializer;
import io.github.astrarre.recipe.internal.vanilla.CustomRecipeType;
import io.github.astrarre.recipe.internal.vanilla.RecipeWrapper;
import io.github.astrarre.recipe.v0.fabric.RecipePostReloadEvent;
import io.github.astrarre.util.v0.api.Val;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
		CustomRecipeType<?> recipeType = new CustomRecipeType<>(recipeId);
		Registry.register(Registry.RECIPE_TYPE, recipeId, recipeType);
		CustomRecipeSerializer<?> serializer = new CustomRecipeSerializer<>(type, recipeType, gson, recipeId);
		ForgeRegistries.RECIPE_SERIALIZERS.register(serializer);
		Val<List<T>> values = new Val<>();
		RecipePostReloadEvent.EVENT.addListener((manager, recipes) -> {
			Map<Identifier, net.minecraft.recipe.Recipe<?>> rec = recipes.get(recipeType);
			List<T> list = new ArrayList<>(rec.size());
			for (net.minecraft.recipe.Recipe<?> value : rec.values()) {
				T instance = ((RecipeWrapper<T>)value).instance;
				list.add(instance);
				instance.onInit();
			}
			values.set(list);
		});

		return new ForwardingList<T>() {
			@Override
			protected List<T> delegate() {
				return values.get();
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

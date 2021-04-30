package io.github.astrarre.recipe.internal.serializer;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.minecraft.recipe.Ingredient;

public class IngredientSerializer implements JsonDeserializer<Ingredient> {
	public static final IngredientSerializer INSTANCE = new IngredientSerializer();
	@Override
	public Ingredient deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return Ingredient.fromJson(json);
	}
}

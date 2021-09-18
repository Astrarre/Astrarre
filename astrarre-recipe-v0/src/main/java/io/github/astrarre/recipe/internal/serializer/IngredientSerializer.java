package io.github.astrarre.recipe.internal.serializer;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class IngredientSerializer implements JsonDeserializer<Ingredient> {
	public static final IngredientSerializer INSTANCE = new IngredientSerializer();
	@Override
	public Ingredient deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if(json.isJsonNull() || (json instanceof JsonObject o && o.size() == 0)) {
			return Ingredient.EMPTY;
		} else if(json.isJsonPrimitive()) {
			String id = json.getAsString();
			Item item = Registry.ITEM.get(new Identifier(id));
			return Ingredient.ofItems(item);
		}
		return Ingredient.fromJson(json);
	}
}

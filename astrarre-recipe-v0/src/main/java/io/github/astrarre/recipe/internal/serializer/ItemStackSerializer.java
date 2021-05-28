package io.github.astrarre.recipe.internal.serializer;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemStackSerializer implements JsonDeserializer<ItemStack> {
	public static final ItemStackSerializer INSTANCE = new ItemStackSerializer();

	@Override
	public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if(json.isJsonPrimitive()) {
			String str = json.getAsString();
			Item item = Registry.ITEM.get(new Identifier(str));
			if(item == Items.AIR) {
				throw new JsonParseException("Invalid item id: " + str);
			}
			return new ItemStack(item);
		} else if(json.isJsonObject()) {
			return ShapedRecipe.outputFromJson((JsonObject) json);
		}
		throw new JsonParseException("Invalid ItemStack " + json);
	}
}

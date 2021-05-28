package io.github.astrarre.recipe.internal.serializer;

import java.lang.reflect.Type;
import java.util.function.Supplier;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class TagSerializer<T> implements JsonDeserializer<Tag<T>> {
	public final Supplier<TagGroup<T>> group;

	public TagSerializer(TagManager manager, RegistryKey<Registry<T>> key) {
		this.group = () -> manager.getOrCreateTagGroup(key);
	}

	@Override
	public Tag<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if(json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
			Tag<T> tag = this.group.get().getTag(new Identifier(json.getAsString()));
			if(tag != null) {
				return tag;
			}
		}
		throw new JsonParseException("Invalid tag: " + json);
	}
}

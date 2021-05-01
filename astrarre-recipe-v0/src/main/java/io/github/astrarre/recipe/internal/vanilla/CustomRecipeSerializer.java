package io.github.astrarre.recipe.internal.vanilla;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import io.github.astrarre.recipe.v0.api.InternalRecipeAccess;
import io.github.astrarre.recipe.v0.api.Recipe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class CustomRecipeSerializer<T extends Recipe> implements RecipeSerializer<RecipeWrapper<T>> {
	protected final Class<T> type;
	protected final RecipeType<?> recipeType;
	protected final Gson gson;

	public CustomRecipeSerializer(Class<T> type, RecipeType<?> recipeType, Gson gson) {
		this.type = type;
		this.recipeType = recipeType;
		this.gson = gson;
	}

	@Override
	public RecipeWrapper<T> read(Identifier id, JsonObject json) {
		T instance = this.gson.fromJson(json, this.type);
		InternalRecipeAccess.set(instance, id);
		return new RecipeWrapper<>(id, instance, this.recipeType, this);
	}

	@Override
	public RecipeWrapper<T> read(Identifier id, PacketByteBuf buf) {
		JsonElement object = Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, buf.readCompoundTag());
		T instance = this.gson.fromJson(object, this.type);
		InternalRecipeAccess.set(instance, id);
		return new RecipeWrapper<>(id, instance, this.recipeType, this);
	}

	@Override
	public void write(PacketByteBuf buf, RecipeWrapper<T> recipe) {
		JsonElement element = this.gson.toJsonTree(recipe.instance);
		buf.writeCompoundTag((CompoundTag) Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, element));
	}
}

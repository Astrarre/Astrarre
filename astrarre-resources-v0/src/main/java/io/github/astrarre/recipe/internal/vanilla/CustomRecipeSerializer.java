package io.github.astrarre.recipe.internal.vanilla;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public record CustomRecipeSerializer<T>(Class<T> type, RecipeType<?> recipeType, Gson gson) implements RecipeSerializer<RecipeWrapper<T>> {
	@Override
	public RecipeWrapper<T> read(Identifier id, JsonObject json) {
		T instance = this.gson.fromJson(json, this.type);
		return new RecipeWrapper<>(id, instance, this.recipeType, this);
	}

	@Override
	public RecipeWrapper<T> read(Identifier id, PacketByteBuf buf) {
		JsonElement object = Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, buf.readNbt());
		T instance = this.gson.fromJson(object, this.type);
		return new RecipeWrapper<>(id, instance, this.recipeType, this);
	}

	@Override
	public void write(PacketByteBuf buf, RecipeWrapper<T> recipe) {
		JsonElement element = this.gson.toJsonTree(recipe.instance);
		buf.writeNbt((NbtCompound) Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, element));
	}
}

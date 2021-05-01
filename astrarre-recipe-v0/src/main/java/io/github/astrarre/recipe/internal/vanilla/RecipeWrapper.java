package io.github.astrarre.recipe.internal.vanilla;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class RecipeWrapper<T> implements Recipe<Inventory> {
	protected final Identifier id;
	public final T instance;
	protected final RecipeType<?> type;
	protected final RecipeSerializer<?> serializer;

	public RecipeWrapper(Identifier id, T instance, RecipeType<?> type, RecipeSerializer<?> serializer) {
		this.id = id;
		this.instance = instance;
		this.type = type;
		this.serializer = serializer;
	}

	@Override
	public boolean matches(Inventory inv, World world) {
		return false;
	}

	@Override
	public ItemStack craft(Inventory inv) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return this.serializer;
	}

	@Override
	public RecipeType<?> getType() {
		return this.type;
	}
}

package io.github.astrarre.transfer.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

@Mixin (Ingredient.class)
public interface IngredientAccess {
	@Accessor("matchingStacks")
	ItemStack[] astrarre_transfer$getMatchingStacks();

	@Invoker("cacheMatchingStacks")
	void astrarre_transfer$callCacheMatchingStacks();
}

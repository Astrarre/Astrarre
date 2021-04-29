package io.github.astrarre.transfer.v0.api.filter;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Insertable;

import net.minecraft.recipe.Ingredient;

public class IngredientFilteringInsertable extends FilteringInsertable<ItemKey> {
	public IngredientFilteringInsertable(Ingredient ingredient, Insertable<ItemKey> delegate) {
		super((object, quantity) -> ingredient.test(object.createItemStack(quantity)), delegate);
	}
}

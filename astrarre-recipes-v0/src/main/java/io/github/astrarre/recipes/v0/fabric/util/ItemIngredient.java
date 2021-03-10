package io.github.astrarre.recipes.v0.fabric.util;

import io.github.astrarre.recipes.v0.fabric.value.ItemMatcher;

public class ItemIngredient {
	public final ItemMatcher matcher;
	public final int amount;

	public ItemIngredient(ItemMatcher matcher, int amount) {
		this.matcher = matcher;
		this.amount = amount;
	}
}

package io.github.astrarre.recipes.v0.fabric.value;

import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.recipes.v0.fabric.value.ItemStackValueParser;

import net.minecraft.item.ItemStack;

public interface FabricValueParsers {
	ValueParser<ItemStack> ITEM_STACK = new ItemStackValueParser();
}

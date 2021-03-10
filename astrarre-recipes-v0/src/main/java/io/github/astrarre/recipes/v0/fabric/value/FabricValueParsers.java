package io.github.astrarre.recipes.v0.fabric.value;

import io.github.astrarre.recipes.internal.mixin.FluidTagsAccessor;
import io.github.astrarre.recipes.v0.api.value.RegistryValueParser;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.recipes.v0.fabric.util.ItemIngredient;
import io.github.astrarre.recipes.v0.fabric.value.ItemStackValueParser;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.registry.Registry;

public interface FabricValueParsers {
	ValueParser<ItemStack> ITEM_STACK = new ItemStackValueParser();
	ValueParser<Item> ITEM = new RegistryValueParser<>(Registry.ITEM);
	ValueParser<Block> BLOCK = new RegistryValueParser<>(Registry.BLOCK);
	ValueParser<Fluid> FLUID = new RegistryValueParser<>(Registry.FLUID);
	TagParser<Block> BLOCK_TAG = new TagParser<>(BlockTags.getTagGroup());
	TagParser<Item> ITEM_TAG = new TagParser<>(ItemTags.getTagGroup());
	TagParser<EntityType<?>> ENTITY_TAG = new TagParser<>(EntityTypeTags.getTagGroup());
	TagParser<Fluid> FLUID_TAG = new TagParser<>(FluidTagsAccessor.getREQUIRED_TAGS().getGroup());
	ValueParser<ItemMatcher> ITEM_MATCHER = new ItemMatcher.Parser();
	ValueParser<ItemIngredient> ITEM_INGREDIENT = new ItemIngredientParser();
}

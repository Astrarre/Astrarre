package io.github.astrarre.recipes.v0.fabric.value;

import io.github.astrarre.recipes.v0.api.value.RegistryValueParser;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.recipes.v0.fabric.util.ItemIngredient;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.util.registry.Registry;

public interface FabricValueParsers {
	ValueParser<ItemStack> ITEM_STACK = new ItemStackValueParser();
	ValueParser<Item> ITEM = new RegistryValueParser<>(Registry.ITEM);
	ValueParser<Block> BLOCK = new RegistryValueParser<>(Registry.BLOCK);
	ValueParser<Fluid> FLUID = new RegistryValueParser<>(Registry.FLUID);
	TagParser<Block> BLOCK_TAG = new TagParser<>(identifier -> ServerTagManagerHolder.getTagManager().getBlocks().getTag(identifier));
	TagParser<Item> ITEM_TAG = new TagParser<>(identifier -> ServerTagManagerHolder.getTagManager().getItems().getTag(identifier));
	TagParser<EntityType<?>> ENTITY_TAG = new TagParser<>(identifier -> ServerTagManagerHolder.getTagManager().getEntityTypes().getTag(identifier));
	TagParser<Fluid> FLUID_TAG = new TagParser<>(identifier -> ServerTagManagerHolder.getTagManager().getFluids().getTag(identifier));
	ValueParser<ItemMatcher> ITEM_MATCHER = new ItemMatcher.Parser();
	ValueParser<ItemIngredient> ITEM_INGREDIENT = new ItemIngredientParser();
}

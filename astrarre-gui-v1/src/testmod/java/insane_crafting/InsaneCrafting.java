package insane_crafting;

import java.util.List;

import io.github.astrarre.recipe.v0.api.Recipes;
import io.github.astrarre.recipe.v0.fabric.RecipePostReloadEvent;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public class InsaneCrafting implements ModInitializer {
	public static final List<InsaneCraftingRecipe> RECIPES = Recipes.createRecipe(id("craft"), InsaneCraftingRecipe.class);
	public static final InsaneCraftingTable TABLE = new InsaneCraftingTable(AbstractBlock.Settings.copy(Blocks.STONE));
	public static final BlockEntityType<InsaneCraftingTable.Tile> TILE = FabricBlockEntityTypeBuilder.create(InsaneCraftingTable.Tile::new, TABLE).build();

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, id("insane_crafting"), TABLE);
		Text text = new TranslatableText("insane_crafting.eye_strain_warning").formatted(Formatting.GRAY);
		Registry.register(Registry.ITEM, id("insane_crafting"), new BlockItem(TABLE, new Item.Settings()) {
			@Override
			public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
				tooltip.add(text);
				super.appendTooltip(stack, world, tooltip, context);
			}
		});
		Registry.register(Registry.BLOCK_ENTITY_TYPE, id("insane_crafting"), TILE);
	}

	public static Identifier id(String path) {
		return new Identifier("insane_crafting", path);
	}
}

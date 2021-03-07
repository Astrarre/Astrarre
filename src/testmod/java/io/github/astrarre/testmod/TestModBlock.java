package io.github.astrarre.testmod;

import io.github.astrarre.recipes.v0.api.ingredient.Ingredients;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;
import io.github.astrarre.recipes.v0.api.recipe.Result;
import io.github.astrarre.recipes.v0.api.util.Val;
import io.github.astrarre.recipes.v0.fabric.ingredient.FabricIngredients;
import io.github.astrarre.recipes.v0.fabric.output.FabricOutputs;
import io.github.astrarre.transfer.internal.inventory.InventoryDelegate;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tickable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

public class TestModBlock extends Block implements BlockEntityProvider {
	public static final Block TEST_BLOCK = Registry.register(Registry.BLOCK,
			"testmod:be_test",
			new TestModBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
	public static final BlockEntityType<?> BE_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE,
			"testmod:be_test",
			BlockEntityType.Builder.create(TestModBlock.Entity::new, TEST_BLOCK).build(null));

	private static final Recipe.Tri<Inventory, Val<Integer>, Inventory> RECIPE = Recipe.builder()
			.add(FabricIngredients.STRICT_STACK)
			.add(Ingredients.INTEGER)
			.outputs(Id.create("mymod", "ore_doubler"))
			.add(FabricOutputs.ITEM_STACK)
			.build("My Mod's Ore Doubler");

	public TestModBlock(Settings settings) {
		super(settings);
	}

	public static void modInit() {
		TestMod.load(RECIPE, "iron_ore + 4 --[mymod:ore_doubler]-> iron_ingot x10");
		// io.github.astrarre.testmod.TestModBlock::modInit
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new Entity();
	}

	public static class Entity extends BlockEntity implements InventoryDelegate, Tickable {
		public final Inventory inventory = new SimpleInventory(1);
		public Val<Integer> progress = Val.ofInteger();
		public Entity() {
			super(BE_TYPE);
		}

		@Override
		public void tick() {
			Inventory temp = new SimpleInventory(1);
			Result result = RECIPE.apply(this.inventory, this.progress, temp);
			if(result.getFailedIndex() == 1) { // if missing processing time
				this.progress.set(this.progress.get() + 1);
			} else if(result.isSuccess()) {
				ItemStack stack = temp.getStack(0);
				Block.dropStack(this.getWorld(), this.getPos(), stack);
			}
		}

		@Override
		public Inventory getDelegate() {
			return this.inventory;
		}
	}
}

package io.github.astrarre.testmod;

import java.util.List;

import io.github.astrarre.gui.v0.api.base.AAggregateDrawable;
import io.github.astrarre.gui.v0.api.base.widgets.ATextFieldWidget;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ASlot;
import io.github.astrarre.gui.v0.rei.REIContainerGUIRecipeCategory;
import io.github.astrarre.gui.v0.rei.container.REICompatibleContainerGUI;
import io.github.astrarre.recipe.v0.rei.RecipeDisplayBuilder;
import io.github.astrarre.util.v0.api.Either;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class TestModREIPlugin implements REIPluginV0 {
	@Override
	public Identifier getPluginIdentifier() {
		return new Identifier("testmod:test_plugin");
	}

	@Override
	public void registerPluginCategories(RecipeHelper recipeHelper) {
		recipeHelper.registerCategory(new REIContainerGUIRecipeCategory<>(
				EntryStack.create(new ItemStack(Items.STONE)),
				new Identifier("test:category"),
				(container, width, height) -> new REICompatibleContainerGUI<RecipeDisplay>(container, width, height) {
					@Override
					protected void addGui(AAggregateDrawable panel, int width, int height, Either<RecipeDisplay, List<ASlot>> context) {
						panel.addClient(new ATextFieldWidget(width, 20));
					}
				}));
	}

	@Override
	public void registerRecipeDisplays(RecipeHelper recipeHelper) {
		for (TestRecipe recipe : TestMod.RECIPE_LIST) {
			RecipeHelper.getInstance().registerDisplay(new RecipeDisplayBuilder<>().addInput(recipe.item).build(recipe, new Identifier("test:category")));
		}
	}
}

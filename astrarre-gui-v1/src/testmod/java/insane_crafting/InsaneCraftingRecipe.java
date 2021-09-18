package insane_crafting;

import java.util.List;

import io.github.astrarre.recipe.v0.api.Recipe;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class InsaneCraftingRecipe extends Recipe {
	public List<List<Ingredient>> input;
	public ItemStack output;

	@Override
	public void onInit() {
	}
}

package insane_crafting;

import io.github.astrarre.recipe.v0.api.Recipe;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.collection.DefaultedList;

public class InsaneCraftingRecipe extends Recipe {
	public static final InsaneCraftingRecipe TEST;
	static {
		DefaultedList<Ingredient> inputs = DefaultedList.ofSize(InsaneInventory.SIZE, Ingredient.ofItems(Items.STICK));
		TEST = new InsaneCraftingRecipe(inputs, new ItemStack(Items.LADDER));
	}

	public final DefaultedList<Ingredient> input;
	public final ItemStack output;

	public InsaneCraftingRecipe(DefaultedList<Ingredient> input, ItemStack output) {
		this.input = input;
		this.output = output;
	}

	@Override
	public void onInit() {
		Validate.isTrue(this.input.size() == InsaneInventory.SIZE, "input size must be 1076");
	}
}

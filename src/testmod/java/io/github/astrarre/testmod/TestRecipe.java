package io.github.astrarre.testmod;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.recipe.v0.api.Recipe;
import io.github.astrarre.recipe.v0.rei.RecipeDisplayBuilder;
import io.github.astrarre.recipe.v0.rei.RecipeDisplays;
import me.shedaniel.rei.api.RecipeHelper;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class TestRecipe extends Recipe implements Provider {
	public final Tag<Item> item;

	public TestRecipe(Tag<Item> item) {this.item = item;}

	@Override
	public void onInit() {

	}

	@Override
	public @Nullable Object get(Access<?> access) {
		if(access == RecipeDisplays.REI_COMPATIBILITY) {
			return new RecipeDisplayBuilder<>()
					.addInput(this.item)
					.build(this, new Identifier("test:category"));
		}
		return null;
	}
}

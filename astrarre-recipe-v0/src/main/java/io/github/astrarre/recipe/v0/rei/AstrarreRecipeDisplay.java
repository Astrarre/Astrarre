package io.github.astrarre.recipe.v0.rei;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import io.github.astrarre.recipe.v0.api.Recipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.impl.display.DisplaySpec;
import org.jetbrains.annotations.NotNull;

import net.minecraft.util.Identifier;

public class AstrarreRecipeDisplay<T extends Recipe> implements Display {
	public final T instance;
	protected final List<EntryIngredient> inputs;
	protected final Identifier category;
	protected final List<EntryIngredient> results;
	protected final List<EntryIngredient> required;
	protected final Optional<Identifier> location;

	/**
	 * @see RecipeDisplayBuilder
	 */
	public AstrarreRecipeDisplay(T instance,
			List<List<EntryStack>> inputs,
			Identifier category,
			List<List<EntryStack>> results,
			List<List<EntryStack>> required) {
		this.instance = instance;
		this.location = Optional.of(instance.getId());
		this.inputs = inputs.stream().map(stacks -> EntryIngredient.of((Iterable) stacks)).toList();
		this.category = category;
		this.results = results.stream().map(stacks -> EntryIngredient.of((Iterable) stacks)).toList();
		this.required = required.stream().map(stacks -> EntryIngredient.of((Iterable) stacks)).toList();
	}

	@Override
	public @NotNull List<EntryIngredient> getInputEntries() {
		return this.inputs;
	}

	@Override
	public List<EntryIngredient> getOutputEntries() {
		return this.results;
	}

	@Override
	public @NotNull CategoryIdentifier<?> getCategoryIdentifier() {
		return CategoryIdentifier.of(this.category);
	}

	@Override
	public @NotNull List<EntryIngredient> getRequiredEntries() {
		return this.required;
	}
}

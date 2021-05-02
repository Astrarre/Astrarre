package io.github.astrarre.recipe.v0.rei;

import java.util.List;
import java.util.Optional;

import io.github.astrarre.recipe.v0.api.Recipe;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import org.jetbrains.annotations.NotNull;

import net.minecraft.util.Identifier;

public class AstrarreRecipeDisplay<T extends Recipe> implements RecipeDisplay {
	public final T instance;
	protected final List<List<EntryStack>> inputs;
	protected final Identifier category;
	protected final List<List<EntryStack>> results;
	protected final List<List<EntryStack>> required;
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
		this.inputs = inputs;
		this.category = category;
		this.results = results;
		this.required = required;
	}

	@Override
	public @NotNull List<List<EntryStack>> getInputEntries() {
		return this.inputs;
	}

	@Override
	public @NotNull Identifier getRecipeCategory() {
		return this.category;
	}

	@Override
	public @NotNull List<List<EntryStack>> getResultingEntries() {
		return this.results;
	}

	@Override
	public @NotNull List<List<EntryStack>> getRequiredEntries() {
		return this.required;
	}

	@Override
	public @NotNull Optional<Identifier> getRecipeLocation() {
		return this.location;
	}
}

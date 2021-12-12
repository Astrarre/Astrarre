package io.github.astrarre.recipe.v0.rei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.types.Func;
import dev.architectury.fluid.FluidStack;
import io.github.astrarre.recipe.v0.api.Recipe;
import io.github.astrarre.util.v0.fabric.Tags;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import org.jetbrains.annotations.Contract;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

/**
 * The backing lists of this builder are not copied, so don't mutate the things you pass in
 */
public class RecipeDisplayBuilder<T extends Recipe> {
	protected final List<List<EntryStack>> inputs = new ArrayList<>();
	protected final List<List<EntryStack>> outputs = new ArrayList<>();
	protected final List<List<EntryStack>> catalysts = new ArrayList<>();

	public static <T extends Recipe> RecipeDisplayBuilder<T> builder() {
		return new RecipeDisplayBuilder<>();
	}

	public AstrarreRecipeDisplay<T> build(T instance, Identifier category) {
		return new AstrarreRecipeDisplay<>(instance, this.inputs, category, this.outputs, this.catalysts);
	}

	/**
	 * @see ImmutableList#of()
	 * @param input a list of the possible inputs for this slot. For example if your recipe had an item tag for an input, this list would be a list of entrystacks for each item
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addInput(List<EntryStack> input) {
		this.inputs.add(input);
		return this;
	}

	public static <T> List<EntryStack> of(Tag<T> tag, Function<T, EntryStack> stackFunction) {
		return new ForwardingList<>() {
			protected List<EntryStack> delegate;

			@Override
			protected List<EntryStack> delegate() {
				if(this.delegate == null) {
					List<EntryStack> stacks = new ArrayList<>();
					for(T item : Tags.get(tag)) {
						stacks.add(stackFunction.apply(item));
					}
					this.delegate = stacks;
				}
				return this.delegate;
			}
		};
	}

	/**
	 * @param input a required item, that is not consumed
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addInput(Tag<Item> input) {
		this.inputs.add(of(input, EntryStacks::of));
		return this;
	}

	/**
	 * @param input a required item, that is not consumed
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addInput(Ingredient input) {
		this.inputs.add((List)EntryIngredients.ofIngredient(input).cast());
		return this;
	}

	/**
	 * @param input a required item, that is not consumed
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addInputStacks(List<ItemStack> input) {
		this.inputs.add((List)input.stream().map(EntryStacks::of).toList());
		return this;
	}

	/**
	 * @param input a required item, that is not consumed
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addInput(ItemStack... input) {
		this.inputs.add((List)Arrays.stream(input).map(EntryStacks::of).toList());
		return this;
	}

	/**
	 * @param input a required item, that is not consumed
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addInput(ItemConvertible... input) {
		this.inputs.add((List)Arrays.stream(input).map(ItemConvertible::asItem).map(ItemStack::new).map(EntryStacks::of).toList());
		return this;
	}

	@Contract("-> this")
	public RecipeDisplayBuilder<T> addEmptyInput() {
		this.inputs.add(ImmutableList.of());
		return this;
	}

	/**
	 * @param droplets 1/81,000 of a bucket
	 */
	@Contract("_,_ -> this")
	public RecipeDisplayBuilder<T> addInput(Tag<Fluid> input, int droplets) {
		this.inputs.add(of(input, fluid -> EntryStacks.of(fluid, droplets)));
		return this;
	}

	/**
	 * @param droplets 1/81,000 of a bucket
	 */
	@Contract("_,_ -> this")
	public RecipeDisplayBuilder<T> addInput(Fluid input, int droplets) {
		this.inputs.add(Collections.singletonList(EntryStacks.of(input, droplets)));
		return this;
	}
	
	// outputs

	/**
	 * @see ImmutableList#of()
	 * @param output a list of the possible outputs for this slot. For example if your recipe had an item tag for an output, this list would be a list of entrystacks for each item
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addOutput(List<EntryStack> output) {
		this.outputs.add(output);
		return this;
	}

	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addOutput(Tag<Item> output) {
		this.outputs.add(of(output, EntryStacks::of));
		return this;
	}

	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addOutput(Ingredient output) {
		this.outputs.add((List)EntryIngredients.ofIngredient(output).cast());
		return this;
	}

	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addOutputStacks(List<ItemStack> output) {
		this.outputs.add((List)output.stream().map(EntryStacks::of).toList());
		return this;
	}

	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addOutput(ItemStack... output) {
		this.outputs.add((List)Arrays.stream(output).map(EntryStacks::of).toList());
		return this;
	}

	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addOutput(ItemConvertible... output) {
		this.outputs.add((List)Arrays.stream(output).map(ItemConvertible::asItem).map(ItemStack::new).map(EntryStacks::of).toList());
		return this;
	}

	@Contract("-> this")
	public RecipeDisplayBuilder<T> addEmptyOutput() {
		this.outputs.add(ImmutableList.of());
		return this;
	}

	/**
	 * @param droplets 1/81,000 of a bucket
	 */
	@Contract("_,_ -> this")
	public RecipeDisplayBuilder<T> addOutput(Tag<Fluid> output, int droplets) {
		this.outputs.add(of(output, fluid -> EntryStacks.of(fluid, droplets)));
		return this;
	}

	/**
	 * @param droplets 1/81,000 of a bucket
	 */
	@Contract("_,_ -> this")
	public RecipeDisplayBuilder<T> addOutput(Fluid output, int droplets) {
		this.outputs.add(Collections.singletonList(EntryStacks.of(output, droplets)));
		return this;
	}
	
	// catalysts

	/**
	 * @see ImmutableList#of()
	 * @param catalyst a list of the possible catalysts for this slot. For example if your recipe had an item tag for an catalyst, this list would be a list of entrystacks for each item
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addCatalyst(List<EntryStack> catalyst) {
		this.catalysts.add(catalyst);
		return this;
	}

	/**
	 * @param catalyst a required item, that is not consumed
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addCatalyst(Tag<Item> catalyst) {
		this.catalysts.add(of(catalyst, EntryStacks::of));
		return this;
	}

	/**
	 * @param catalyst a required item, that is not consumed
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addCatalyst(Ingredient catalyst) {
		this.catalysts.add((List)EntryIngredients.ofIngredient(catalyst).cast());
		return this;
	}

	/**
	 * @param catalyst a required item, that is not consumed
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addCatalystStacks(List<ItemStack> catalyst) {
		this.catalysts.add((List)catalyst.stream().map(EntryStacks::of).toList());
		return this;
	}

	/**
	 * @param catalyst a required item, that is not consumed
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addCatalyst(ItemStack... catalyst) {
		this.catalysts.add((List)Arrays.stream(catalyst).map(EntryStacks::of).toList());
		return this;
	}

	/**
	 * @param catalyst a required item, that is not consumed
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addCatalyst(ItemConvertible... catalyst) {
		this.catalysts.add((List)Arrays.stream(catalyst).map(ItemConvertible::asItem).map(ItemStack::new).map(EntryStacks::of).toList());
		return this;
	}

	@Contract("-> this")
	public RecipeDisplayBuilder<T> addEmptyCatalyst() {
		this.catalysts.add(ImmutableList.of());
		return this;
	}

	/**
	 * @param droplets 1/81,000 of a bucket
	 */
	@Contract("_,_ -> this")
	public RecipeDisplayBuilder<T> addCatalyst(Tag<Fluid> catalyst, int droplets) {
		this.catalysts.add(of(catalyst, fluid -> EntryStacks.of(fluid, droplets)));
		return this;
	}

	/**
	 * @param droplets 1/81,000 of a bucket
	 */
	@Contract("_,_ -> this")
	public RecipeDisplayBuilder<T> addCatalyst(Fluid catalyst, int droplets) {
		this.catalysts.add(Collections.singletonList(EntryStacks.of(catalyst, droplets)));
		return this;
	}


}

package io.github.astrarre.recipe.v0.rei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import io.github.astrarre.recipe.v0.api.Recipe;
import io.github.astrarre.util.v0.fabric.Tags;
import me.shedaniel.rei.api.EntryStack;
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

	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addInput(Tag<Item> input) {
		this.inputs.add(EntryStack.ofItems((Collection) Tags.get(input)));
		return this;
	}

	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addInput(Ingredient input) {
		this.inputs.add(EntryStack.ofIngredient(input));
		return this;
	}

	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addInputStacks(List<ItemStack> input) {
		this.inputs.add(EntryStack.ofItemStacks(input));
		return this;
	}

	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addInput(ItemStack... input) {
		this.inputs.add(EntryStack.ofItemStacks(Arrays.asList(input)));
		return this;
	}

	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addInput(ItemConvertible... input) {
		this.inputs.add(EntryStack.ofItems(Arrays.asList(input)));
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
		List<EntryStack> stacks = new ArrayList<>();
		for (Fluid fluid : Tags.get(input)) {
			EntryStack stack = EntryStack.create(fluid, droplets);
			stack.setting(EntryStack.Settings.Fluid.AMOUNT_TOOLTIP, stack1 ->  I18n.translate("tooltip.rei.fluid_amount", stack1.getAmount()));
			stacks.add(stack);
		}
		this.inputs.add(stacks);
		return this;
	}

	/**
	 * @param droplets 1/81,000 of a bucket
	 */
	@Contract("_,_ -> this")
	public RecipeDisplayBuilder<T> addInput(Fluid input, int droplets) {
		this.inputs.add(Collections.singletonList(EntryStack.create(input, droplets)));
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
		this.outputs.add(EntryStack.ofItems((Collection) Tags.get(output)));
		return this;
	}

	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addOutput(Ingredient output) {
		this.outputs.add(EntryStack.ofIngredient(output));
		return this;
	}

	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addOutputStacks(List<ItemStack> output) {
		this.outputs.add(EntryStack.ofItemStacks(output));
		return this;
	}

	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addOutput(ItemStack... output) {
		this.outputs.add(EntryStack.ofItemStacks(Arrays.asList(output)));
		return this;
	}

	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addOutput(ItemConvertible... output) {
		this.outputs.add(EntryStack.ofItems(Arrays.asList(output)));
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
		List<EntryStack> stacks = new ArrayList<>();
		for (Fluid fluid : Tags.get(output)) {
			EntryStack stack = EntryStack.create(fluid, droplets);
			stack.setting(EntryStack.Settings.Fluid.AMOUNT_TOOLTIP, stack1 ->  I18n.translate("tooltip.rei.fluid_amount", stack1.getAmount()));
			stacks.add(stack);
		}
		this.outputs.add(stacks);
		return this;
	}

	/**
	 * @param droplets 1/81,000 of a bucket
	 */
	@Contract("_,_ -> this")
	public RecipeDisplayBuilder<T> addOutput(Fluid output, int droplets) {
		this.outputs.add(Collections.singletonList(EntryStack.create(output, droplets)));
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
		this.catalysts.add(EntryStack.ofItems((Collection) Tags.get(catalyst)));
		return this;
	}

	/**
	 * @param catalyst a required item, that is not consumed
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addCatalyst(Ingredient catalyst) {
		this.catalysts.add(EntryStack.ofIngredient(catalyst));
		return this;
	}

	/**
	 * @param catalyst a required item, that is not consumed
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addCatalystStacks(List<ItemStack> catalyst) {
		this.catalysts.add(EntryStack.ofItemStacks(catalyst));
		return this;
	}

	/**
	 * @param catalyst a required item, that is not consumed
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addCatalyst(ItemStack... catalyst) {
		this.catalysts.add(EntryStack.ofItemStacks(Arrays.asList(catalyst)));
		return this;
	}

	/**
	 * @param catalyst a required item, that is not consumed
	 */
	@Contract("_ -> this")
	public RecipeDisplayBuilder<T> addCatalyst(ItemConvertible... catalyst) {
		this.catalysts.add(EntryStack.ofItems(Arrays.asList(catalyst)));
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
		List<EntryStack> stacks = new ArrayList<>();
		for (Fluid fluid : Tags.get(catalyst)) {
			EntryStack stack = EntryStack.create(fluid, droplets);
			stack.setting(EntryStack.Settings.Fluid.AMOUNT_TOOLTIP, stack1 ->  I18n.translate("tooltip.rei.fluid_amount", stack1.getAmount()));
			stacks.add(stack);
		}
		this.catalysts.add(stacks);
		return this;
	}

	/**
	 * @param droplets 1/81,000 of a bucket
	 */
	@Contract("_,_ -> this")
	public RecipeDisplayBuilder<T> addCatalyst(Fluid catalyst, int droplets) {
		this.catalysts.add(Collections.singletonList(EntryStack.create(catalyst, droplets)));
		return this;
	}


}

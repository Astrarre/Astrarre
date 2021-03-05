package io.github.astrarre.recipes.v0.api;

import io.github.astrarre.recipes.v0.api.value.ValueParser;

/**
 * An input (or output) parser of a recipe
 */
public interface RecipePart<V, I> {
	// for transactions, have a 'global' transaction that's shared among all the tests
	// if any one of them fails, they abort the transaction
	//      if the output suceedes, you commit the transaction
	//      if the output is never called then there is problem
	// todo debug mode for transactions, record stacktrace

	ValueParser<V> parser();

	/**
	 * @param val the 'ingredient' (eg. ItemStack[])
	 * @param inp the 'inventory' (eg. CraftingInventory)
	 * @return true if the input was satisfactory
	 */
	boolean test(I inp, V val);

	void apply(I inp, V val);
}

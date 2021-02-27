package io.github.astrarre.recipies.v0.api.ingredient;

import io.github.astrarre.recipies.internal.AsciiPrimeFilter;
import io.github.astrarre.recipies.v0.api.io.CharInput;
import org.apache.commons.lang3.mutable.MutableInt;

public class IntIngredient implements RecipeComponentParser<Integer, MutableInt> {
	@Override
	public int end(CharInput input) {
		int chars = 0;
		while (AsciiPrimeFilter.INSTANCE.test(input.read(), AsciiPrimeFilter.INTEGER)) {
			chars++;
		}
		return chars;
	}

	@Override
	public Integer parse(CharInput input) {
		int mul = 1;
		int val = 0;
		int c;
		input.mark(1);
		while (AsciiPrimeFilter.INSTANCE.test(c = input.read(), AsciiPrimeFilter.INTEGER)) {
			if(c == '-') {
				mul = -1;
			} else {
				val *= 10;
				val += c - '0';
			}
			input.mark(1);
		}
		input.reset();
		return val * mul;
	}

	@Override
	public boolean apply(Integer value, MutableInt input) {
		if(value > 3) {
			input.setValue(value);
			return true;
		} else {
			return false;
		}
	}
}

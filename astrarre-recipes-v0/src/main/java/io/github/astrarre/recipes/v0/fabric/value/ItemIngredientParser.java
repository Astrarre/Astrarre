package io.github.astrarre.recipes.v0.fabric.value;

import io.github.astrarre.recipes.v0.api.util.Either;
import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.recipes.v0.fabric.util.ItemIngredient;

public class ItemIngredientParser implements ValueParser<ItemIngredient> {
	@Override
	public Either<ItemIngredient, String> parse(PeekableReader reader) {
		Either<ItemMatcher, String> either = FabricValueParsers.ITEM_MATCHER.parse(reader);
		if(either.hasLeft()) {
			ItemMatcher matcher = either.getLeft();
			ValueParser.skipWhitespace(reader, 10);
			if(reader.peek() == 'x') {
				reader.read();
				Either<Integer, String> count = ValueParser.INTEGER.parse(reader);
				if(count.hasLeft()) {
					return Either.ofLeft(new ItemIngredient(matcher, count.getLeft()));
				} else {
					return Either.ofRight("invalid quantity");
				}
			} else {
				return Either.ofLeft(new ItemIngredient(matcher, 1));
			}
		} else {
			return either.asLeft();
		}
	}
}

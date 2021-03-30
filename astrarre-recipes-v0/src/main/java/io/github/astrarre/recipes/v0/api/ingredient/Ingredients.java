package io.github.astrarre.recipes.v0.api.ingredient;

import io.github.astrarre.recipes.v0.api.value.ValueParser;

public interface Ingredients {
	// subtract ingredients, require an amount of a type, and if found, subtracts
	SubtractPart.Double DOUBLE = new SubtractPart.Double();
	SubtractPart.Integer INTEGER = new SubtractPart.Integer();
	SubtractPart.Float FLOAT = new SubtractPart.Float();
	SubtractPart.Long LONG = new SubtractPart.Long();
	// require ingredients, require an amount of a type, but doesn't subtract
	RequirePart<Float> REQUIRE_FLOAT = new RequirePart<>(ValueParser.FLOAT);
	RequirePart<Double> REQUIRE_DOUBLE = new RequirePart<>(ValueParser.DOUBLE);
	RequirePart<Integer> REQUIRE_INT = new RequirePart<>(ValueParser.INTEGER);
	RequirePart<Long> REQUIRE_LONG = new RequirePart<>(ValueParser.LONG);
}

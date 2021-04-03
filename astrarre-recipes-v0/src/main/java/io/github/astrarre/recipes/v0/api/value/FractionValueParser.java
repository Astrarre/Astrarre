package io.github.astrarre.recipes.v0.api.value;

import io.github.astrarre.util.v0.api.Either;
import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

public class FractionValueParser implements ValueParser<Fraction> {
	@Override
	public @NotNull Either<Fraction, String> parse(PeekableReader reader) {
		Either<Integer, String> numerator = INTEGER.parse(reader);
		if (numerator.hasRight()) {
			return Either.ofRight("invalid numerator");
		}

		ValueParser.skipWhitespace(reader, 10);
		if (reader.read() == '/') {
			ValueParser.skipWhitespace(reader, 10);
			Either<Integer, String> denominator = INTEGER.parse(reader);
			if (denominator.hasRight()) {
				return Either.ofRight("invalid denominator");
			}

			return Either.ofLeft(Fraction.getFraction(numerator.getLeft(), denominator.getLeft()));
		}

		return Either.ofLeft(Fraction.getFraction(numerator.getLeft(), 1));
	}

}

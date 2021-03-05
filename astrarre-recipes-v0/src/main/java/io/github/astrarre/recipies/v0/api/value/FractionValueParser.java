package io.github.astrarre.recipies.v0.api.value;

import io.github.astrarre.recipies.v0.api.util.Either;
import io.github.astrarre.recipies.v0.api.util.PeekableReader;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

public class FractionValueParser implements ValueParser<Fraction> {
	@Override
	public @NotNull Either<Fraction, String> parse(PeekableReader reader) {
		PeekableReader peek = reader.createSubReader();
		Either<Integer, String> numerator = INTEGER.parse(peek);
		if (numerator.hasRight()) {
			reader.abort(peek);
			return Either.ofRight("invalid numerator");
		}

		ValueParser.skipWhitespace(peek, 10);
		if (peek.read() == '/') {
			ValueParser.skipWhitespace(peek, 10);
			Either<Integer, String> denominator = INTEGER.parse(peek);
			if (denominator.hasRight()) {
				reader.abort(peek);
				return Either.ofRight("invalid denominator");
			}

			reader.commit(peek);
			return Either.ofLeft(Fraction.getFraction(numerator.getLeft(), denominator.getLeft()));
		}

		reader.commit(peek);
		return Either.ofLeft(Fraction.getFraction(numerator.getLeft(), 1));
	}

	@Override
	public String name() {
		return "fraction";
	}
}

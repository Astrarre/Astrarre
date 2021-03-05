package io.github.astrarre.recipes.internal.elements;

import io.github.astrarre.recipes.v0.api.util.Either;
import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import io.github.astrarre.recipes.v0.api.value.ValueParser;

import net.minecraft.util.Unit;

public final class PlusParser implements ValueParser<Unit> {
	public static final PlusParser INSTANCE = new PlusParser();

	private PlusParser() {}
	@Override
	public Either<Unit, String> parse(PeekableReader reader) {
		if(reader.peek() == '+') {
			reader.read();
			return Either.ofLeft(Unit.INSTANCE);
		} else {
			return Either.ofRight("No '+' was found to conjoin parameters!");
		}
	}

}

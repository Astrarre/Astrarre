package io.github.astrarre.recipes.internal.elements;

import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.util.v0.api.Either;

import net.minecraft.util.Unit;

public class StandardArrowParser implements ValueParser<Unit> {
	private static final char[] BUF = "-->".toCharArray();

	@Override
	public Either<Unit, String> parse(PeekableReader reader) {
		char[] buf = PeekableReader.CHAR_BUFFERS.get();
		int read = reader.read(buf, 0, BUF.length);
		if(!IdentifiedArrowParser.isEqual(BUF, buf, 0, read)) {
			return Either.ofRight("invalid arrow");
		}
		return Either.ofLeft(Unit.INSTANCE);
	}
}

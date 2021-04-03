package io.github.astrarre.recipes.internal.elements;

import io.github.astrarre.util.v0.api.Either;
import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Unit;

public final class IdentifiedArrowParser implements ValueParser<Unit> {
	private static final char[] ARROW_START = "--[".toCharArray(), ARROW_END = "]->".toCharArray();

	private final Id identifier;
	public IdentifiedArrowParser(Id identifier) {
		this.identifier = identifier;
	}

	@Override
	public Either<Unit, String> parse(PeekableReader reader) {
		char[] buf = PeekableReader.CHAR_BUFFERS.get();
		int read = reader.read(buf, 0, ARROW_START.length);
		if(!isEqual(ARROW_START, buf, 0, read)) {
			return Either.ofRight("invalid arrow (invalid start)");
		}

		PeekableReader sub = reader.createSubReader();
		Either<Id, String> id = ValueParser.ID.parse(sub);
		if(id.hasLeft() && this.identifier.equals(id.getLeft())) {
			int r = sub.read(buf, 0, ARROW_END.length);
			if(!isEqual(ARROW_END, buf, 0, r)) {
				reader.abort(sub);
				return Either.ofRight("invalid arrow (invalid end)");
			}
			reader.commit(sub);
			return Either.ofLeft(Unit.INSTANCE);
		} else {
			reader.abort(sub);
			return Either.ofRight("invalid arrow (invalid id)");
		}
	}

	public static boolean isEqual(char[] inp, char[] b, int off, int len) {
		if(inp.length != len) {
			return false;
		}

		for (int i = 0; i < len; i++) {
			if(inp[i] != b[i + off]) {
				return false;
			}
		}

		return true;
	}
}

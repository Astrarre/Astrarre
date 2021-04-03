package io.github.astrarre.recipes.v0.api.value;

import io.github.astrarre.recipes.internal.mixin.IdentifierAccess;
import io.github.astrarre.util.v0.api.Either;
import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class IdentifierParser implements ValueParser<Id>  {
	@Override
	public Either<Id, String> parse(PeekableReader reader) {
		int ni = 0;
		char last;
		while (IdentifierAccess.callIsNamespaceCharacterValid(last = (char) reader.peek(ni++))) {
		}

		String name = reader.peekString(ni - 1);
		Id id;

		try {
			if (last == ':') {
				StringBuilder builder = new StringBuilder();
				char c;
				while (Identifier.isPathCharacterValid(c = (char) reader.peek(ni++))) {
					builder.append(c);
				}
				id = Id.create(name, builder.toString());
			} else {
				id = Id.create("minecraft", name);
			}
		} catch (InvalidIdentifierException e) {
			return Either.ofRight(e.getMessage());
		}

		reader.readString(ni - 1);
		return Either.ofLeft(id);
	}

}

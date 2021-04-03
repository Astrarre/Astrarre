package io.github.astrarre.recipes.v0.fabric.value;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.astrarre.util.v0.api.Either;
import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import org.jetbrains.annotations.NotNull;

public final class BrigadierValueParser<V> implements ValueParser<V> {
	private final int maxCount;
	private final String name;
	private final Read<V> reader;

	public BrigadierValueParser(int count, String name, Read<V> reader) {
		this.maxCount = count;
		this.name = name;
		this.reader = reader;
	}

	public interface Read<V> {
		V apply(StringReader reader) throws CommandSyntaxException;
	}

	@Override
	public @NotNull Either<V, String> parse(PeekableReader input) {
		try {
			String read = input.peekString(this.maxCount);
			StringReader reader = new StringReader(read);
			V val = this.reader.apply(reader);
			input.skip(reader.getCursor());
			return Either.ofLeft(val);
		} catch (CommandSyntaxException e) {
			return Either.ofRight("invalid " + this.name);
		}
	}

}

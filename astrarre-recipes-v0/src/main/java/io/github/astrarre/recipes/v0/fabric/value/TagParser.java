package io.github.astrarre.recipes.v0.fabric.value;

import java.util.function.Function;
import java.util.function.Supplier;

import io.github.astrarre.util.v0.api.Either;
import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class TagParser<T> implements ValueParser<Tag<T>> {
	protected final Function<Identifier, Tag<T>> tag;

	public TagParser(Function<Identifier, Tag<T>> tag) {
		this.tag = tag;
	}

	@Override
	public Either<Tag<T>, String> parse(PeekableReader reader) {
		if(reader.read() == '#') {
			Either<Id, String> id = ValueParser.ID.parse(reader);
			if(id.hasLeft()) {
				Tag<T> tag = this.tag.apply(id.getLeft().to());
				return tag == null ? Either.ofRight("no tag for id " + id.getLeft()) : Either.ofLeft(tag);
			}
			return id.asLeft();
		}
		return Either.ofRight("tag does not start with '#'");
	}
}

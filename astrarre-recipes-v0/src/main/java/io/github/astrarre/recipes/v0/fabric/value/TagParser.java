package io.github.astrarre.recipes.v0.fabric.value;

import io.github.astrarre.recipes.v0.api.util.Either;
import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;

public class TagParser<T> implements ValueParser<Tag<T>> {
	protected final TagGroup<T> group;

	public TagParser(TagGroup<T> group) {
		this.group = group;
	}

	@Override
	public Either<Tag<T>, String> parse(PeekableReader reader) {
		if(reader.read() == '#') {
			Either<Id, String> id = ValueParser.ID.parse(reader);
			if(id.hasLeft()) {
				return Either.ofLeft(this.group.getTag(id.getLeft().to()));
			}
			return id.asLeft();
		}
		return Either.ofRight("tag does not start with '#'");
	}
}

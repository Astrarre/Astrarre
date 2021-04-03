package io.github.astrarre.recipes.internal.recipe;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.util.v0.api.Either;
import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import io.github.astrarre.recipes.v0.api.value.ValueParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Unit;

public class RecipeParser {
	private static final Logger LOGGER = LogManager.getLogger("RecipeParser");

	private final List<RecipeImpl> recipes;

	public RecipeParser(List<RecipeImpl> recipes) {
		this.recipes = recipes;
	}

	public void parseToCompletion(InputStream stream, String fileName) {
		this.parseToCompletion(new InputStreamReader(stream), fileName);
	}

	public void parseToCompletion(Reader reader, String fileName) {
		this.parseToCompletion(new PeekableReader(reader), fileName);
	}

	public void parseToCompletion(PeekableReader reader, String fileName) {
		while (true) {
			ValueParser.skipWhitespace(reader, 100);
			if(reader.peek() == -1) {
				return;
			}
			this.parse(reader, fileName);
		}
	}

	public void parse(PeekableReader reader, String fileName) {
		List<String> errors = new ArrayList<>();
		List<RecipeImpl> list = this.recipes;
		outer:
		for (int i = 0; i < list.size(); i++) {
			errors.add("");
			RecipeImpl recipe = list.get(i);
			PeekableReader read = reader.createSubReader();
			List<Object> inputs = new ArrayList<>();
			for (Object o : recipe.list) {
				ValueParser.skipWhitespace(read, 10);
				Either<?, String> val;
				if (o instanceof ValueParser) {
					val = ((ValueParser<Unit>) o).parse(read);
				} else {
					val = ((RecipePart<?, ?>) o).parser().parse(read);
					if (val.hasLeft()) {
						inputs.add(val.getLeft());
						continue;
					}
				}

				if (val.hasRight()) {
					String error = val.getRight();
					errors.set(i, error);
					reader.abort(read);
					continue outer;
				}
			}

			reader.commit(read);
			recipe.values.add(inputs);
			return;
		}

		LOGGER.warn("Unable to parse recipe! (" + fileName + ")");
		for (int i = 0; i < errors.size(); i++) {
			LOGGER.warn("\t" + list.get(i).name + ": " + errors.get(i));
		}
		throw new IllegalArgumentException();
	}
}


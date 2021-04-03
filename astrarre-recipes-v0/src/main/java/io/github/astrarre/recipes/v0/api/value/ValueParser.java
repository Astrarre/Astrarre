package io.github.astrarre.recipes.v0.api.value;

import com.mojang.brigadier.StringReader;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.astrarre.util.v0.api.Either;
import io.github.astrarre.recipes.v0.api.util.PeekableReader;
import io.github.astrarre.recipes.v0.fabric.value.BrigadierValueParser;
import io.github.astrarre.util.v0.api.Id;
import org.apache.commons.lang3.math.Fraction;

import net.minecraft.nbt.StringNbtReader;

public interface ValueParser<V> {
	ValueParser<Integer> INTEGER = new BrigadierValueParser<>(11, "integer", StringReader::readInt);
	ValueParser<Double> DOUBLE = new BrigadierValueParser<>(64, "decimal (double)", StringReader::readDouble);
	ValueParser<Long> LONG = new BrigadierValueParser<>(22, "integer (long)", StringReader::readLong);
	ValueParser<Float> FLOAT = new BrigadierValueParser<>(32, "decimal", StringReader::readFloat);
	ValueParser<String> WORD = new BrigadierValueParser<>(128, "word", StringReader::readString);
	ValueParser<Fraction> FRACTION = new FractionValueParser();
	ValueParser<NBTagView> NBT = new BrigadierValueParser<>(1024, "NBTag", reader -> FabricViews.view(new StringNbtReader(reader).parseCompoundTag()));
	ValueParser<Id> ID = new IdentifierParser();

	static int skipWhitespace(PeekableReader reader, int maxWhitespace) {
		for (int i = 0; i < maxWhitespace; i++) {
			if (!Character.isWhitespace(reader.peek())) {
				return i;
			}
			reader.read();
		}
		return maxWhitespace;
	}

	/**
	 * peek by default, actually read the data if there
	 *
	 * @return the information of this ingredient, or an error message
	 */
	Either<V, String> parse(PeekableReader reader);

}

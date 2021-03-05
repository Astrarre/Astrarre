package io.github.astrarre.recipies.v0.api.part;

import io.github.astrarre.recipies.v0.api.util.Val;
import io.github.astrarre.recipies.v0.api.value.ValueParser;

public abstract class SubtractPart<V extends Comparable<V>> implements RecipePart<V, Val<V>> {
	protected final ValueParser<V> parser;
	protected SubtractPart(ValueParser<V> parser) {
		this.parser = parser;
	}

	@Override
	public ValueParser<V> parser() {
		return this.parser;
	}

	@Override
	public boolean test(Val<V> value, V val) {
		return value.get().compareTo(val) > 0;
	}
	
	public final static class Double extends SubtractPart<java.lang.Double> {
		public Double() {
			super(ValueParser.DOUBLE);
		}

		@Override
		public void apply(Val<java.lang.Double> input, java.lang.Double value) {
			input.set(input.get() - value);
		}
	}

	public final static class Integer extends SubtractPart<java.lang.Integer> {
		public Integer() {
			super(ValueParser.INTEGER);
		}

		@Override
		public void apply(Val<java.lang.Integer> input, java.lang.Integer value) {
			input.set(input.get() - value);
		}
	}

	public final static class Long extends SubtractPart<java.lang.Long> {
		public Long() {
			super(ValueParser.LONG);
		}

		@Override
		public void apply(Val<java.lang.Long> input, java.lang.Long value) {
			input.set(input.get() - value);
		}
	}

	public final static class Float extends SubtractPart<java.lang.Float> {
		public Float() {
			super(ValueParser.FLOAT);
		}

		@Override
		public void apply(Val<java.lang.Float> input, java.lang.Float value) {
			input.set(input.get() - value);
		}
	}

	public final static class Fraction extends SubtractPart<org.apache.commons.lang3.math.Fraction> {
		public Fraction() {
			super(ValueParser.FRACTION);
		}

		@Override
		public void apply(Val<org.apache.commons.lang3.math.Fraction> input, org.apache.commons.lang3.math.Fraction value) {
			input.set(input.get().subtract(value));
		}
	}
}

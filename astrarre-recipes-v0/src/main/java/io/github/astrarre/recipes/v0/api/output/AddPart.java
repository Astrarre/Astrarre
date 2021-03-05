package io.github.astrarre.recipes.v0.api.output;

import io.github.astrarre.recipes.v0.api.RecipePart;
import io.github.astrarre.recipes.v0.api.util.Val;
import io.github.astrarre.recipes.v0.api.value.ValueParser;

public abstract class AddPart<V extends Comparable<V>> implements RecipePart<V, Val<V>> {
	private final ValueParser<V> parser;

	protected AddPart(ValueParser<V> parser) {this.parser = parser;}

	@Override
	public ValueParser<V> parser() {
		return this.parser;
	}

	@Override
	public boolean test(Val<V> inp, V val) {
		return true;
	}

	public final static class Double extends AddPart<java.lang.Double> {
		public Double() {
			super(ValueParser.DOUBLE);
		}

		@Override
		public void apply(Val<java.lang.Double> inp, java.lang.Double val) {
			inp.set(inp.get() + val);
		}
	}

	public final static class Integer extends AddPart<java.lang.Integer> {
		public Integer() {
			super(ValueParser.INTEGER);
		}

		@Override
		public void apply(Val<java.lang.Integer> inp, java.lang.Integer val) {
			inp.set(inp.get() + val);
		}
	}

	public final static class Long extends AddPart<java.lang.Long> {
		public Long() {
			super(ValueParser.LONG);
		}

		@Override
		public void apply(Val<java.lang.Long> inp, java.lang.Long val) {
			inp.set(inp.get() + val);
		}
	}

	public final static class Float extends AddPart<java.lang.Float> {
		public Float() {
			super(ValueParser.FLOAT);
		}

		@Override
		public void apply(Val<java.lang.Float> inp, java.lang.Float val) {
			inp.set(inp.get() + val);
		}
	}

	public final static class Fraction extends AddPart<org.apache.commons.lang3.math.Fraction> {
		public Fraction() {
			super(ValueParser.FRACTION);
		}

		@Override
		public void apply(Val<org.apache.commons.lang3.math.Fraction> inp, org.apache.commons.lang3.math.Fraction val) {
			inp.set(inp.get().add(val));
		}
	}
}

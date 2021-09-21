package io.github.astrarre.util.internal.factory;

public record FieldValue(String name, Object value) {
	public static FieldValue of(String name, Object value) {
		return new FieldValue(name, value);
	}
}

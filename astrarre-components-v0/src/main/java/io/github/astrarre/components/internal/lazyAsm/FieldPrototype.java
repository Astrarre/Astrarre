package io.github.astrarre.components.internal.lazyAsm;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;

public final class FieldPrototype {
	public final String type, name;
	public final Object value;

	/**
	 * @param value {@link ClassVisitor#visitField(int, String, String, String, Object)}
	 */
	public FieldPrototype(String type, String name, @Nullable Object value) {
		this.type = type;
		this.name = name;
		this.value = value;
	}
}
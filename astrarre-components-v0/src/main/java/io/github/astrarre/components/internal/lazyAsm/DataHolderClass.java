package io.github.astrarre.components.internal.lazyAsm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import io.github.astrarre.components.internal.util.FieldPrototype;
import org.jetbrains.annotations.Nullable;

public final class DataHolderClass {
	@Nullable public final DataHolderClass parent;
	public final int version;
	public final List<FieldPrototype> fields = new ArrayList<>();
	public final String name;
	public Supplier<?> compiled;

	public DataHolderClass(@Nullable DataHolderClass parent, int version, String name) {
		this.parent = parent;
		this.version = version;
		this.name = name;
	}
}

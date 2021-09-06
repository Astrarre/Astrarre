package io.github.astrarre.util.v0.api.collection;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.collection.DefaultedList;

/**
 * default list but it's protected constructors are visible
 * @param <T>
 */
public class ExposedDefaultList<T> extends DefaultedList<T> {
	public ExposedDefaultList(List<T> delegate, @Nullable T initialElement) {
		super(delegate, initialElement);
	}
}

package io.github.astrarre.transfer.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NUtil {
	public static <T> Set<T> addAll(@Nullable Set<T> current, Collection<T> collection) {
		if(!collection.isEmpty()) {
			if(current == null) current = new HashSet<>();
			current.addAll(collection);
		}
		return current;
	}
}

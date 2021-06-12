package io.github.astrarre.event.internal.core;

import java.util.Objects;

import io.github.astrarre.event.v0.api.core.ContextHolder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiContextHolderImpl<T> implements ContextHolder<T> {
	protected final ThreadLocal<ObjectArrayList<T>> active = ThreadLocal.withInitial(ObjectArrayList::new);
	protected final String name;

	public MultiContextHolderImpl(String name) {
		this.name = name;
	}

	@Override
	public void push(@NotNull T value) {
		this.active.get().push(value);
	}

	@Override
	public @NotNull T pop(@Nullable T ref) {
		T pop = this.active.get().pop();
		if(ref != null && !Objects.equals(pop, ref)) {
			throw new IllegalStateException("stack corruption in '" + this.name + "' expected: " + ref + " found: " + pop);
		}
		return pop;
	}

	@Override
	public @Nullable T swap(@Nullable T value) {
		ObjectArrayList<T> list = this.active.get();
		if(list.isEmpty()) {
			list.push(value);
			return null;
		} else if(value == null) {
			return list.pop();
		} else {
			return list.set(list.size() - 1, value);
		}
	}

	@Override
	public @Nullable T getNth(int index) {
		ObjectArrayList<T> list = this.active.get();
		int size = list.size();
		if(index < size) {
			return list.get(size - index);
		} else {
			return null;
		}
	}

	@Override
	public int size() {
		return this.active.get().size();
	}
}

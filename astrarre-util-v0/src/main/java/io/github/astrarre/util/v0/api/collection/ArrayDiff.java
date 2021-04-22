package io.github.astrarre.util.v0.api.collection;

import java.util.AbstractList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * a list that keeps track of the changes to the array instead of mutating the backing list
 * @param <T>
 */
public class ArrayDiff<T> extends AbstractList<T> {
	protected final List<T> root;
	public final Int2ObjectMap<T> changes = new Int2ObjectOpenHashMap<>();
	public ArrayDiff(List<T> root) {
		this.root = root;
	}

	@Override
	public T get(int index) {
		T at = this.changes.get(index);
		if(at == null) {
			return this.root.get(index);
		}
		return at;
	}

	@Override
	public T set(int index, T element) {
		T changes = this.changes.put(index, element);
		if(changes == null) {
			return this.root.get(index);
		}
		return changes;
	}

	@Override
	public int size() {
		return this.root.size();
	}
}

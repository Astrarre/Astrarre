package io.github.astrarre.gui.internal.util;

import java.util.AbstractSet;
import java.util.Iterator;

public final class IntFlags<T extends Flag> extends AbstractSet<T> {
	final T[] allValues;
	final boolean mutable;
	public int flags;
	int size = -1;

	public IntFlags(T[] values, int flag, boolean mutable) {
		this.allValues = values;
		this.flags = flag;
		this.mutable = mutable;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<>() {
			int index;
			T next;

			@Override
			public boolean hasNext() {
				return (this.next = this.findNext()) != null;
			}

			@Override
			public T next() {
				return this.findNext();
			}

			@Override
			public void remove() {
				if(IntFlags.this.mutable) {
					T flag = IntFlags.this.allValues[this.index];
					IntFlags.this.flags &= ~flag.flag();
				} else {
					Iterator.super.remove();
				}
			}

			private T findNext() {
				T curr = this.next;
				if(curr == null) {
					T next = null;
					int index;
					while((index = ++this.index) < IntFlags.this.allValues.length) {
						if(IntFlags.this.contains(next = IntFlags.this.allValues[index])) {
							break;
						}
					}
					return next;
				} else {
					return curr;
				}
			}
		};
	}

	@Override
	public int size() {
		int size = this.size;
		if(size == -1) {
			size = 0;
			for(T t : this) {
				size++;
			}
			this.size = size;
		}
		return size;
	}

	@Override
	public boolean contains(Object o) {
		return o instanceof Flag flag && (flag.flag() & this.flags) != 0;
	}

	@Override
	public boolean add(T t) {
		if(this.mutable) {
			boolean changed = (t.flag() & this.flags) == 0;
			this.flags |= t.flag();
			return changed;
		} else {
			return super.add(t);
		}
	}

	@Override
	public boolean remove(Object o) {
		if(this.mutable && o instanceof Flag t) {
			boolean changed = (t.flag() & this.flags) != 0;
			this.flags &= ~t.flag();
			return changed;
		} else {
			return super.remove(o);
		}
	}
}

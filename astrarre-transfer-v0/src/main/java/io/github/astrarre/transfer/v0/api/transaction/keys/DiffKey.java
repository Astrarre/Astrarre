package io.github.astrarre.transfer.v0.api.transaction.keys;

import java.util.HashMap;

import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.util.v0.api.collection.MapDiff;

/**
 * a key who's type is mutable, but can be 'diffed' and 'patched'.
 */
public abstract class DiffKey<T> extends ObjectKeyImpl<T> {
	public DiffKey(T originalValue) {
		super(originalValue);
	}

	public DiffKey() {
	}

	@Override
	public void onApply(Transaction transaction) {
		this.handler.pop(transaction);
		Transaction parent = transaction.getParent();
		T old = this.values.pop();
		T replacement = this.get(parent);
		this.patch(old, replacement);
	}

	@Override
	public T get(Transaction transaction) {
		T original = super.get(transaction);
		if (transaction != null && !this.handler.isTop(transaction)) {
			T differ = this.createDiffer(original);
			this.set(transaction, differ);
			return differ;
		}
		return original;
	}

	protected abstract void patch(T diff, T target);

	protected abstract T createDiffer(T original);

	public static class Map<K, V> extends DiffKey<java.util.Map<K, V>> {
		public Map(java.util.Map<K, V> originalValue) {
			super(originalValue);
		}

		public Map() {
			super(new HashMap<>());
		}

		@Override
		protected java.util.Map<K, V> createDiffer(java.util.Map<K, V> original) {
			return new MapDiff<>(original);
		}

		@Override
		protected void patch(java.util.Map<K, V> diff, java.util.Map<K, V> target) {
			MapDiff<K, V> casted = (MapDiff) diff;
			casted.removed.forEach(target::remove);
			target.putAll(casted.added);
		}
	}
}

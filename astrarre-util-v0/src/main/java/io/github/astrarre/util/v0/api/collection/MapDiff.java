package io.github.astrarre.util.v0.api.collection;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

/**
 * a 'view only' mutable wrapper over a map
 */
public final class MapDiff<K, V> extends ForwardingMap<K, V> {
	public final Map<K, V> original;
	public final Map<K, V> added = new HashMap<>();
	public Set<K> removed = new HashSet<>();

	public MapDiff(Map<K, V> original) {
		this.original = Collections.unmodifiableMap(original);
	}

	@Override
	protected Map<K, V> delegate() {
		return this.original;
	}

	@Override
	public int size() {
		return this.added.size() + Sets.difference(this.original.keySet(), this.removed).size();
	}

	@Override
	public boolean isEmpty() {
		return this.added.isEmpty() && Sets.difference(this.original.keySet(), this.removed).isEmpty();
	}

	@Override
	public V remove(Object object) {
		V old = this.added.remove(object);
		this.removed.add((K) object);
		return old == null ? this.original.get(object) : old;
	}

	@Override
	public void clear() {
		this.removed = new HashSet<>(this.original.keySet());
		this.added.clear();
	}

	@Override
	public boolean containsKey(java.lang.Object object) {
		return this.added.containsKey(object) || (this.original.containsKey(object) && !this.removed.contains(object));
	}

	@Override
	public boolean containsValue(Object value) {
		for (Entry<K, V> entry : this.entrySet()) {
			if (Objects.equals(entry.getValue(), value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public V get(Object key) {
		V val = this.added.get(key);
		if (val == null && !this.removed.contains(key)) {
			return this.original.get(key);
		}
		return val;
	}

	@Override
	public V put(K key, V value) {
		this.removed.remove(key);
		V replaced = this.added.put(key, value);
		return replaced == null ? this.original.get(key) : replaced;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		this.added.putAll(map);
		this.removed.removeAll(map.keySet());
	}

	@Override
	public Set<K> keySet() {
		return new AbstractSet<K>() {
			@Override
			public Iterator<K> iterator() {
				return MapDiff.this.entrySet().stream().map(Entry::getKey).iterator();
			}

			@Override
			public int size() {
				return (int) MapDiff.this.entrySet().stream().map(Entry::getKey).count();
			}

			@Override
			public boolean contains(Object o) {
				return MapDiff.this.containsKey(o);
			}
		};
	}

	@Override
	public Collection<V> values() {
		return Collections2.transform(this.entrySet(), Entry::getValue);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new AbstractSet<Entry<K, V>>() {
			@Override
			public Iterator<Entry<K, V>> iterator() {
				return new Iterator<Entry<K, V>>() {
					final Set<Entry<K, V>> visited = new HashSet<>();
					final Iterator<Entry<K, V>> addedIterator = MapDiff.this.added.entrySet().iterator();
					final Iterator<Entry<K, V>> iterator = Iterators.concat(this.addedIterator,
							Iterators.filter(MapDiff.this.original.entrySet().iterator(),
									o -> !(MapDiff.this.removed.contains(o.getKey()) || MapDiff.this.added.containsKey(o.getKey()))));

					Entry<K, V> curr;
					boolean wasAdded;

					@Override
					public boolean hasNext() {
						return this.iterator.hasNext();
					}

					@Override
					public Entry<K, V> next() {
						if (this.addedIterator.hasNext()) {
							this.wasAdded = true;
							this.curr = this.iterator.next();
						} else this.curr = new Entry<K, V>() {
							final Entry<K, V> entry = iterator.next();
							V valOverride = this.entry.getValue();
							@Override
							public K getKey() {
								return this.entry.getKey();
							}

							@Override
							public V getValue() {
								return this.valOverride;
							}

							@Override
							public V setValue(V value) {
								V ov = this.valOverride;
								this.valOverride = value;
								MapDiff.this.put(this.entry.getKey(), value);
								return ov;
							}
						};

						this.visited.add(this.curr);
						return this.curr;
					}

					@Override
					public void remove() {
						if (this.wasAdded) {
							this.addedIterator.remove();
						} else {
							MapDiff.this.added.remove(this.curr.getKey());
							MapDiff.this.removed.add(this.curr.getKey());
						}
					}
				};
			}

			@Override
			public int size() {
				return MapDiff.this.size();
			}

			@Override
			public boolean contains(Object o) {
				if(!(o instanceof Entry)) return false;
				Entry entry = (Entry) o;
				return Objects.equals(MapDiff.this.get(entry.getKey()), entry.getValue());
			}
		};
	}

	@Override
	public boolean equals(Object object) {
		return this.standardEquals(object);
	}

	@Override
	public int hashCode() {
		return this.standardHashCode();
	}

	@Override
	public boolean remove(Object key, Object value) {
		this.added.remove(key, value);
		if (Objects.equals(this.original.get(key), value)) {
			return this.removed.add((K) key);
		}
		return false;
	}

	@Override
	public String toString() {
		return this.standardToString();
	}
}
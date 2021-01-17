package io.github.astrarre.itemview.internal.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.Tag;

public class NBTagUnmodifiableMap implements Map<String, Tag> {
	private final Map<String, Tag> delegate;
	private Set<String> keys;
	private Collection<Tag> values;
	private Set<Entry<String, Tag>> entrySet;

	public NBTagUnmodifiableMap(Map<String, Tag> delegate) {this.delegate = delegate;}

	private IllegalStateException unsupported() {
		return new IllegalStateException("cannot modify immutable compound tag!");
	}

	@Override
	public int size() {
		return this.delegate.size();
	}

	@Override
	public boolean isEmpty() {
		return this.delegate.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.delegate.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.delegate.containsValue(value);
	}

	@Override
	public Tag get(Object key) {
		return this.delegate.get(key);
	}

	@Nullable
	@Override
	public Tag put(String key, Tag value) {
		throw this.unsupported();
	}

	@Override
	public Tag remove(Object key) {
		throw this.unsupported();
	}

	@Override
	public void putAll(@NotNull Map<? extends String, ? extends Tag> m) {
		throw this.unsupported();
	}

	@Override
	public void clear() {
		throw this.unsupported();
	}

	@NotNull
	@Override
	public Set<String> keySet() {
		if(this.keys == null) {
			this.keys = Collections.unmodifiableSet(this.delegate.keySet());
		}
		return this.keys;
	}

	@NotNull
	@Override
	public Collection<Tag> values() {
		if(this.values == null) {
			this.values = Collections.unmodifiableCollection(this.delegate.values());
		}
		return this.values;
	}

	@NotNull
	@Override
	public Set<Entry<String, Tag>> entrySet() {
		if(this.entrySet == null) {
			this.entrySet = Collections.unmodifiableSet(this.delegate.entrySet());
		}
		return this.entrySet;
	}

	@Override
	public boolean equals(Object o) {
		return o == this || (o instanceof NBTagUnmodifiableMap && ((NBTagUnmodifiableMap) o).delegate == this.delegate);
	}

	@Override
	public int hashCode() {
		return this.delegate.hashCode();
	}

	@Override
	public Tag getOrDefault(Object key, Tag defaultValue) {
		return this.delegate.getOrDefault(key, defaultValue);
	}

	@Override
	public void forEach(BiConsumer<? super String, ? super Tag> action) {
		this.delegate.forEach(action);
	}

	@Override
	public void replaceAll(BiFunction<? super String, ? super Tag, ? extends Tag> function) {
		throw this.unsupported();
	}

	@Nullable
	@Override
	public Tag putIfAbsent(String key, Tag value) {
		throw this.unsupported();
	}

	@Override
	public boolean remove(Object key, Object value) {
		throw this.unsupported();
	}

	@Override
	public boolean replace(String key, Tag oldValue, Tag newValue) {
		throw this.unsupported();
	}

	@Nullable
	@Override
	public Tag replace(String key, Tag value) {
		throw this.unsupported();
	}

	@Override
	public Tag computeIfAbsent(String key, Function<? super String, ? extends Tag> mappingFunction) {
		throw this.unsupported();
	}

	@Override
	public Tag computeIfPresent(String key, BiFunction<? super String, ? super Tag, ? extends Tag> remappingFunction) {
		throw this.unsupported();
	}

	@Override
	public Tag compute(String key, BiFunction<? super String, ? super Tag, ? extends Tag> remappingFunction) {
		throw this.unsupported();
	}

	@Override
	public Tag merge(String key, Tag value, BiFunction<? super Tag, ? super Tag, ? extends Tag> remappingFunction) {
		throw this.unsupported();
	}
}

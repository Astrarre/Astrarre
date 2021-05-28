package io.github.astrarre.itemview.internal.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NBTagUnmodifiableMap implements Map<String, NbtElement> {
	private final Map<String, NbtElement> delegate;
	private Set<String> keys;
	private Collection<NbtElement> values;
	private Set<Entry<String, NbtElement>> entrySet;

	public NBTagUnmodifiableMap(Map<String, NbtElement> delegate) {this.delegate = delegate;}

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
	public NbtElement get(Object key) {
		return this.delegate.get(key);
	}

	@Nullable
	@Override
	public NbtElement put(String key, NbtElement value) {
		throw this.unsupported();
	}

	@Override
	public NbtElement remove(Object key) {
		throw this.unsupported();
	}

	@Override
	public void putAll(@NotNull Map<? extends String, ? extends NbtElement> m) {
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
	public Collection<NbtElement> values() {
		if(this.values == null) {
			this.values = Collections.unmodifiableCollection(this.delegate.values());
		}
		return this.values;
	}

	@NotNull
	@Override
	public Set<Entry<String, NbtElement>> entrySet() {
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
	public NbtElement getOrDefault(Object key, NbtElement defaultValue) {
		return this.delegate.getOrDefault(key, defaultValue);
	}

	@Override
	public void forEach(BiConsumer<? super String, ? super NbtElement> action) {
		this.delegate.forEach(action);
	}

	@Override
	public void replaceAll(BiFunction<? super String, ? super NbtElement, ? extends NbtElement> function) {
		throw this.unsupported();
	}

	@Nullable
	@Override
	public NbtElement putIfAbsent(String key, NbtElement value) {
		throw this.unsupported();
	}

	@Override
	public boolean remove(Object key, Object value) {
		throw this.unsupported();
	}

	@Override
	public boolean replace(String key, NbtElement oldValue, NbtElement newValue) {
		throw this.unsupported();
	}

	@Nullable
	@Override
	public NbtElement replace(String key, NbtElement value) {
		throw this.unsupported();
	}

	@Override
	public NbtElement computeIfAbsent(String key, Function<? super String, ? extends NbtElement> mappingFunction) {
		throw this.unsupported();
	}

	@Override
	public NbtElement computeIfPresent(String key, BiFunction<? super String, ? super NbtElement, ? extends NbtElement> remappingFunction) {
		throw this.unsupported();
	}

	@Override
	public NbtElement compute(String key, BiFunction<? super String, ? super NbtElement, ? extends NbtElement> remappingFunction) {
		throw this.unsupported();
	}

	@Override
	public NbtElement merge(String key, NbtElement value, BiFunction<? super NbtElement, ? super NbtElement, ? extends NbtElement> remappingFunction) {
		throw this.unsupported();
	}
}

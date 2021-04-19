package io.github.astrarre.access.v0.fabric;

import com.google.common.collect.Iterators;
import io.github.astrarre.access.internal.MapFilter;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.access.v0.api.func.IterFunc;
import io.github.astrarre.access.v0.fabric.func.ItemFunction;
import io.github.astrarre.access.v0.fabric.provider.ItemProvider;
import io.github.astrarre.itemview.v0.fabric.ItemKey;

import net.minecraft.item.Item;

public class ItemAccess<C, T> extends Access<ItemFunction<C, T>> {
	private final MapFilter<Item, ItemFunction<C, T>> itemTypes;
	private final MapFilter<ItemKey, ItemFunction<C, T>> itemKeyTypes;
	private boolean addedProviderFunction;

	public ItemAccess() {
		this((T) null);
	}

	/**
	 * docs for each of the constructors are the same from FunctionAccess
	 *
	 * @see FunctionAccess
	 */
	public ItemAccess(T defaultValue) {
		this(arr -> (direction, key, count, container) -> {
			for (ItemFunction<C, T> function : arr) {
				T value = function.get(direction, key, count, container);
				if (value != null) {
					return value;
				}
			}
			return defaultValue;
		});
	}

	public ItemAccess(IterFunc<ItemFunction<C, T>> combiner) {
		super(combiner);
		this.itemTypes = new MapFilter<>(combiner);
		this.itemKeyTypes = new MapFilter<>(combiner);
	}

	public static <C, T> ItemAccess<C, T> newInstance(IterFunc<T> combiner) {
		return new ItemAccess<>((functions) -> (direction, key, count, container) -> combiner.combine(() -> Iterators.transform(functions.iterator(),
				input -> input.get(direction, key, count, container))));
	}

	/**
	 * adds functions for {@link ItemProvider}
	 *
	 * (calling this multiple times will only register it once)
	 */
	public ItemAccess<C, T> addItemProviderFunctions() {
		if (this.addedProviderFunction) {
			return this;
		}
		this.addedProviderFunction = true;
		this.andThen((direction, key, count, container) -> {
			Item item = key.getItem();
			if (item instanceof ItemProvider) {
				return (T) ((ItemProvider) item).get(this, direction, key, count, container);
			}
			return null;
		});
		return this;
	}

	public ItemAccess<C, T> forItem(Item item, ItemFunction<C, T> function) {
		if (this.itemTypes.add(item, function)) {
			this.andThen((direction, key, count, container) -> this.itemTypes.get(key.getItem()).get(direction, key, count, container));
		}
		return this;
	}

	/**
	 * registers a filter for an item that exactly matches the ItemKey
	 */
	public ItemAccess<C, T> forItemKey(ItemKey item, ItemFunction<C, T> function) {
		if (this.itemKeyTypes.add(item, function)) {
			this.andThen((direction, itemKey, count, container) -> this.itemKeyTypes.get(itemKey).get(direction, itemKey, count, container));
		}
		return this;
	}
}

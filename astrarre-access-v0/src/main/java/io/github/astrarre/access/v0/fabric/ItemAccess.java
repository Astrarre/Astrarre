package io.github.astrarre.access.v0.fabric;

import com.google.common.collect.Iterators;
import io.github.astrarre.access.internal.MapFilter;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.access.v0.api.func.IterFunc;
import io.github.astrarre.access.v0.fabric.func.ItemFunction;
import io.github.astrarre.access.v0.fabric.provider.ItemProvider;
import io.github.astrarre.itemview.v0.fabric.ItemKey;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class ItemAccess<T, C> extends Access<ItemFunction<T, C>> {
	private final MapFilter<Item, ItemFunction<T, C>> itemTypes;
	private final MapFilter<Block, ItemFunction<T, C>> blockTypes;
	private final MapFilter<ItemKey, ItemFunction<T, C>> itemKeyTypes;
	private final MapFilter<Class<? extends Item>, ItemFunction<T, C>> itemClassTypes;
	private final MapFilter<Class<? extends Block>, ItemFunction<T, C>> blockClassTypes;
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
			for (ItemFunction<T, C> function : arr) {
				T value = function.get(direction, key, count, container);
				if (value != null) {
					return value;
				}
			}
			return defaultValue;
		});
	}

	public ItemAccess(IterFunc<ItemFunction<T, C>> combiner) {
		super(combiner);
		this.itemTypes = new MapFilter<>(combiner);
		this.itemKeyTypes = new MapFilter<>(combiner);
		this.itemClassTypes = new MapFilter<>(combiner);
		this.blockTypes = new MapFilter<>(combiner);
		this.blockClassTypes = new MapFilter<>(combiner);
	}

	public static <T, C> ItemAccess<T, C> newInstance(IterFunc<T> combiner) {
		return new ItemAccess<>((functions) -> (direction, key, count, container) -> combiner.combine(() -> Iterators.transform(functions.iterator(),
				input -> input.get(direction, key, count, container))));
	}

	/**
	 * adds functions for {@link ItemProvider}
	 *
	 * (calling this multiple times will only register it once)
	 */
	public ItemAccess<T, C> addItemProviderFunctions() {
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

	public ItemAccess<T, C> forItem(Item item, ItemFunction<T, C> function) {
		if (this.itemTypes.add(item, function)) {
			this.andThen((direction, key, count, container) -> this.itemTypes.get(key.getItem()).get(direction, key, count, container));
		}
		return this;
	}

	public ItemAccess<T, C> forItems(ItemFunction<T, C> function, Item... items) {
		for (Item item : items) {
			this.forItem(item, function);
		}
		return this;
	}

	/**
	 * registers a filter for an item that exactly matches the ItemKey
	 */
	public ItemAccess<T, C> forItemKey(ItemKey item, ItemFunction<T, C> function) {
		if (this.itemKeyTypes.add(item, function)) {
			this.andThen((direction, itemKey, count, container) -> this.itemKeyTypes.get(itemKey).get(direction, itemKey, count, container));
		}
		return this;
	}

	public ItemAccess<T, C> forItemKeys(ItemFunction<T, C> function, ItemKey... keys) {
		for (ItemKey key : keys) {
			this.forItemKey(key, function);
		}
		return this;
	}

	/**
	 * registers a filter for an item that exactly matches the ItemKey
	 */
	public ItemAccess<T, C> forItemClassExact(Class<? extends Item> item, ItemFunction<T, C> function) {
		if (this.itemClassTypes.add(item, function)) {
			this.andThen((direction, itemKey, count, container) -> this.itemClassTypes.get(itemKey.getItem().getClass()).get(direction, itemKey, count, container));
		}
		return this;
	}

	public ItemAccess<T, C> forBlockItem(Block item, ItemFunction<T, C> function) {
		if (this.blockTypes.add(item, function)) {
			this.andThen((direction, key, count, container) -> {
				Item i = key.getItem();
				if(i instanceof BlockItem) {
					return this.blockTypes.get(((BlockItem) i).getBlock()).get(direction, key, count, container);
				}
				return null;
			});
		}
		return this;
	}

	public ItemAccess<T, C> forBlockItems(ItemFunction<T, C> function, Block... items) {
		for (Block item : items) {
			this.forBlockItem(item, function);
		}
		return this;
	}

	public ItemAccess<T, C> forBlockItemClassExact(Class<? extends Block> cls, ItemFunction<T, C> function) {
		if (this.blockClassTypes.add(cls, function)) {
			this.andThen((direction, key, count, container) -> {
				Item i = key.getItem();
				if(i instanceof BlockItem) {
					return this.blockClassTypes.get(((BlockItem) i).getBlock().getClass()).get(direction, key, count, container);
				}
				return null;
			});
		}
		return this;
	}

	public ItemAccess<T, C> forBlockItemClassesExact(ItemFunction<T, C> function, Class<? extends Block>... items) {
		for (Class<? extends Block> item : items) {
			this.forBlockItemClassExact(item, function);
		}
		return this;
	}

}

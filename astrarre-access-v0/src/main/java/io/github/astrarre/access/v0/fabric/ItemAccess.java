package io.github.astrarre.access.v0.fabric;

import com.google.common.collect.Iterators;
import io.github.astrarre.access.v0.api.MapFilter;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.access.v0.fabric.helper.ItemAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;
import io.github.astrarre.access.v0.fabric.func.ItemFunction;
import io.github.astrarre.access.v0.fabric.provider.ItemProvider;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class ItemAccess<T, C> extends Access<ItemFunction<T, C>> {
	private final MapFilter<ItemKey, ItemFunction<T, C>> itemKeyTypes;
	private final ItemAccessHelper<ItemKey, ItemFunction<T, C>> items;
	private boolean addedProviderFunction;

	public ItemAccess(Id id) {
		this(id, (T) null);
	}


	/**
	 * docs for each of the constructors are the same from FunctionAccess
	 *
	 * @see FunctionAccess
	 */
	public ItemAccess(Id id, T defaultValue) {
		this(id, ItemFunction.skipIfNull(defaultValue));
	}

	public ItemAccess(Id id, IterFunc<ItemFunction<T, C>> combiner) {
		super(id, combiner);
		IterFunc<ItemFunction<T, C>> comb = ItemFunction.skipIfNull(null);
		this.items = new ItemAccessHelper<>(comb, function -> this.andThen((direction, key, count, c) -> {
			var func = function.apply(key);
			return func != null ? func.get(direction, key, count, c) : null;
		}), ItemKey::getItem);
		this.itemKeyTypes = new MapFilter<>(comb);
	}

	public static <T, C> ItemAccess<T, C> newInstance(Id id, IterFunc<T> combiner) {
		return new ItemAccess<>(id, (functions) -> (direction, key, count, container) -> combiner.combine(() -> Iterators.transform(functions.iterator(),
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

	/**
	 * The item advanced filtering helper. It is recommended you use these for performance's sake
	 */
	public ItemAccessHelper<ItemKey, ItemFunction<T, C>> getItemHelper() {
		return this.items;
	}

	/**
	 * @see #getItemHelper()
	 */
	public ItemAccess<T, C> forItem(Item item, ItemFunction<T, C> function) {
		this.getItemHelper().getItem().forInstanceWeak(item, function);
		return this;
	}

	/**
	 * @see #getItemHelper()
	 */
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
	 * @see #getItemHelper()
	 */
	public ItemAccess<T, C> forItemClassExact(Class<? extends Item> item, ItemFunction<T, C> function) {
		this.getItemHelper().getItem().forClassExact(item, function);
		return this;
	}

	/**
	 * @see #getItemHelper()
	 */
	public ItemAccess<T, C> forBlockItem(Block item, ItemFunction<T, C> function) {
		this.getItemHelper().getBlockItem().getBlock().forInstanceWeak(item, function);
		return this;
	}

	/**
	 * @see #getItemHelper()
	 */
	public ItemAccess<T, C> forBlockItems(ItemFunction<T, C> function, Block... items) {
		for (Block item : items) {
			this.forBlockItem(item, function);
		}
		return this;
	}

	/**
	 * @see #getItemHelper()
	 */
	public ItemAccess<T, C> forBlockItemClassExact(Class<? extends Block> cls, ItemFunction<T, C> function) {
		this.getItemHelper().getBlockItem().getBlock().forClassExact(cls, function);
		return this;
	}

	/**
	 * @see #getItemHelper()
	 */
	@SafeVarargs
	public final ItemAccess<T, C> forBlockItemClassesExact(ItemFunction<T, C> function, Class<? extends Block>... items) {
		for (Class<? extends Block> item : items) {
			this.forBlockItemClassExact(item, function);
		}
		return this;
	}

}

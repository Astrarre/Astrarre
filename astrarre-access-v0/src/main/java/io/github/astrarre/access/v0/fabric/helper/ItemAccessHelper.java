package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.AbstractAccessHelper;
import io.github.astrarre.access.v0.api.helper.AccessHelpers;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ItemAccessHelper<F> extends AbstractAccessHelper<Item, F> {
	protected final FunctionAccessHelper<Item, F> item;
	protected final RegistryAccessHelper<Item, F> itemRegistry;
	protected final TaggedAccessHelper<Item, F> itemTag;
	protected final BlockAccessHelper<F> blockItem;


	public ItemAccessHelper(AccessHelpers.Context<Item, F> copyFrom) {
		super(copyFrom);
		this.item = new FunctionAccessHelper<>(copyFrom);
		this.itemTag = new TaggedAccessHelper<>(copyFrom);
		this.blockItem = new BlockAccessHelper<>(copyFrom.map(i -> i instanceof BlockItem b ? b.getBlock() : null));
		this.itemRegistry = new RegistryAccessHelper<>(Registry.ITEM, copyFrom);
	}

	public FunctionAccessHelper<Item, F> getItem() {
		return this.item;
	}

	public TaggedAccessHelper<Item, F> getItemTag() {
		return this.itemTag;
	}

	/**
	 * advanced filters based on block items
	 */
	public BlockAccessHelper<F> getBlockItem() {
		return this.blockItem;
	}

	public RegistryAccessHelper<Item, F> getItemRegistry() {
		return this.itemRegistry;
	}
}

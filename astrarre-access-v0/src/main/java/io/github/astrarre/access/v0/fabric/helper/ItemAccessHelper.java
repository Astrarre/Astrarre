package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public class ItemAccessHelper<I, F> {
	protected final FunctionAccessHelper<I, Item, F> item;
	protected final TaggedAccessHelper<I, Item, F> itemTag;
	protected final BlockAccessHelper<Item, F> blockItem;

	public ItemAccessHelper(IterFunc<F> func, Consumer<Function<I, F>> adder, Function<I, Item> extract) {
		this(func, adder, extract, null);
	}

	public ItemAccessHelper(IterFunc<F> func, Consumer<Function<I, F>> adder, Function<I, Item> extract, F empty) {
		this.item = new FunctionAccessHelper<>(func, adder, extract, empty);
		this.itemTag = new TaggedAccessHelper<>(func, adder, extract, empty);
		this.blockItem = new BlockAccessHelper<>(func, function -> adder.accept(i -> function.apply(extract.apply(i))), o -> {
			if ((o instanceof BlockItem)) {
				return ((BlockItem) o).getBlock();
			} else {
				return null;
			}
		}, empty);
	}

	public FunctionAccessHelper<I, Item, F> getItem() {
		return this.item;
	}

	public TaggedAccessHelper<I, Item, F> getItemTag() {
		return this.itemTag;
	}

	/**
	 * advanced filters based on block items
	 */
	public BlockAccessHelper<Item, F> getBlockItem() {
		return this.blockItem;
	}
}

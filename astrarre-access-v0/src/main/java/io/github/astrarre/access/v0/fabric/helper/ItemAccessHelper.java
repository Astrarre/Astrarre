package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public class ItemAccessHelper<F> {
	protected final FunctionAccessHelper<Item, F> item;
	protected final TaggedAccessHelper<Item, F> itemTag;
	protected final BlockAccessHelper<F> blockItem;

	/**
	 * creates a new function helper who's incoming type is not the same as the type being filtered
	 */
	public static <I, F> ItemAccessHelper<F> create(IterFunc<F> func, Consumer<Function<I, F>> adder, Function<I, Item> mapper, F empty) {
		return new ItemAccessHelper<>(func, function -> adder.accept(i -> function.apply(mapper.apply(i))), empty);
	}

	/**
	 * creates a new function helper who's incoming type is not the same as the type being filtered
	 */
	public static <I, F> ItemAccessHelper<F> create(IterFunc<F> func, Consumer<Function<I, F>> adder, Function<I, Item> mapper) {
		return create(func, adder, mapper, null);
	}

	public static <I, F> ItemAccessHelper<F> create(Access<F> func, Function<Function<I, F>, F> and, Function<I, Item> mapper, F empty) {
		return new ItemAccessHelper<>(func, function -> and.apply(i -> function.apply(mapper.apply(i))), empty);
	}

	public static <I, F> ItemAccessHelper<F> create(Access<F> func, Function<Function<I, F>, F> and, Function<I, Item> mapper) {
		return create(func, and, mapper, null);
	}

	public ItemAccessHelper(Access<F> func, Function<Function<Item, F>, F> and, F empty) {
		this(func.combiner, f -> func.andThen(and.apply(f)), empty);
	}

	public ItemAccessHelper(Access<F> func, Function<Function<Item, F>, F> adder) {
		this(func, adder, null);
	}

	public ItemAccessHelper(IterFunc<F> func, Consumer<Function<Item, F>> adder) {
		this(func, adder, null);
	}

	public ItemAccessHelper(IterFunc<F> func, Consumer<Function<Item, F>> adder, F empty) {
		this.item = new FunctionAccessHelper<>(func, adder, empty);
		this.itemTag = new TaggedAccessHelper<>(func, adder, empty);
		this.blockItem = BlockAccessHelper.create(func, function -> adder.accept(i -> function.apply(i)), o -> {
			if ((o instanceof BlockItem)) {
				return ((BlockItem) o).getBlock();
			} else {
				return null;
			}
		}, empty);
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
}

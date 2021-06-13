package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.internal.AccessInternal;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;

/**
 * Advanced filtering for Blocks
 */
public class BlockAccessHelper<F> {
	protected final FunctionAccessHelper<Block, F> block;
	protected final FunctionAccessHelper<Fluid, F> fluid;
	protected final TaggedAccessHelper<Block, F> blockTag;
	protected final TaggedAccessHelper<Fluid, F> fluidTag;
	/**
	 * creates a new function helper who's incoming type is not the same as the type being filtered
	 */
	public static <I, F> BlockAccessHelper<F> create(IterFunc<F> func, Consumer<Function<I, F>> adder, Function<I, Block> mapper, F empty) {
		return new BlockAccessHelper<>(func, function -> adder.accept(i -> function.apply(mapper.apply(i))), empty);
	}

	/**
	 * creates a new function helper who's incoming type is not the same as the type being filtered
	 */
	public static <I, F> BlockAccessHelper<F> create(IterFunc<F> func, Consumer<Function<I, F>> adder, Function<I, Block> mapper) {
		return new BlockAccessHelper<>(func, function -> adder.accept(i -> function.apply(mapper.apply(i))), null);
	}

	public static <I, F> BlockAccessHelper<F> create(Access<F> func, Function<Function<I, F>, F> and, Function<I, Block> mapper, F empty) {
		return new BlockAccessHelper<>(func, function -> and.apply(i -> function.apply(mapper.apply(i))), empty);
	}

	public static <I, F> BlockAccessHelper<F> create(Access<F> func, Function<Function<I, F>, F> and, Function<I, Block> mapper) {
		return create(func, and, mapper, null);
	}

	public BlockAccessHelper(Access<F> func, Function<Function<Block, F>, F> and, F empty) {
		this(func.combiner, f -> func.andThen(and.apply(f)), empty);
	}

	public BlockAccessHelper(Access<F> func, Function<Function<Block, F>, F> adder) {
		this(func, adder, null);
	}

	public BlockAccessHelper(IterFunc<F> func, Consumer<Function<Block, F>> adder) {
		this(func, adder, null);
	}

	public BlockAccessHelper(IterFunc<F> func, Consumer<Function<Block, F>> adder, F empty) {
		this.block = new FunctionAccessHelper<>(func, adder, empty);
		this.fluid = FunctionAccessHelper.create(func, adder, AccessInternal::from, empty);
		this.blockTag = new TaggedAccessHelper<>(func, adder, empty);
		this.fluidTag = TaggedAccessHelper.create(func, adder, AccessInternal::from, empty);
	}

	/**
	 * Filtering for Block instances
	 */
	public FunctionAccessHelper<Block, F> getBlock() {
		return this.block;
	}

	/**
	 * Filtering by Block tag
	 */
	public TaggedAccessHelper<Block, F> getBlockTag() {
		return this.blockTag;
	}

	/**
	 * Filtering by Fluid instances
	 */
	public TaggedAccessHelper<Fluid, F> getFluidTag() {
		return this.fluidTag;
	}

	/**
	 * Filtering by Fluid tag
	 */
	public FunctionAccessHelper<Fluid, F> getFluid() {
		return this.fluid;
	}
}

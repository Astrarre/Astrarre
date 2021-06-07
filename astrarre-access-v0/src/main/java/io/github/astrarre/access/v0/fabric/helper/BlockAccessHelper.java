package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.internal.AccessInternal;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;

/**
 * Advanced filtering for Blocks
 */
public class BlockAccessHelper<I, F> {
	protected final FunctionAccessHelper<I, Block, F> block;
	protected final FunctionAccessHelper<I, Fluid, F> fluid;
	protected final TaggedAccessHelper<I, Block, F> blockTag;
	protected final TaggedAccessHelper<I, Fluid, F> fluidTag;

	public BlockAccessHelper(IterFunc<F> func, Consumer<Function<I, F>> adder, Function<I, Block> extract) {
		this(func, adder, extract,null);
	}

	public BlockAccessHelper(IterFunc<F> func, Consumer<Function<I, F>> adder, Function<I, Block> extract, F empty) {
		this.block = new FunctionAccessHelper<>(func, adder, extract, empty);
		this.fluid = new FunctionAccessHelper<>(func, adder, input -> AccessInternal.from(extract.apply(input)), empty);
		this.blockTag = new TaggedAccessHelper<>(func, adder, extract, empty);
		this.fluidTag = new TaggedAccessHelper<>(func, adder, input -> AccessInternal.from(extract.apply(input)), empty);
	}

	/**
	 * Filtering for Block instances
	 */
	public FunctionAccessHelper<I, Block, F> getBlock() {
		return this.block;
	}

	/**
	 * Filtering by Block tag
	 */
	public TaggedAccessHelper<I, Block, F> getBlockTag() {
		return this.blockTag;
	}

	/**
	 * Filtering by Fluid instances
	 */
	public TaggedAccessHelper<I, Fluid, F> getFluidTag() {
		return this.fluidTag;
	}

	/**
	 * Filtering by Fluid tag
	 */
	public FunctionAccessHelper<I, Fluid, F> getFluid() {
		return this.fluid;
	}
}

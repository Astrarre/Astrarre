package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.internal.AccessInternal;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.AbstractInputAccessHelper;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.registry.Registry;

/**
 * Advanced filtering for Blocks
 */
public class BlockAccessHelper<F> extends AbstractInputAccessHelper<Block, F> {
	protected final RegistryAccessHelper<Block, F> blockRegistry;
	protected final RegistryAccessHelper<Fluid, F> fluidRegistry;
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

	/**
	 * creates a new function helper who's incoming type is not the same as the type being filtered
	 */
	public static <I, F> BlockAccessHelper<F> create(AbstractInputAccessHelper<I, F> copyFrom, Function<I, Block> mapper) {
		return new BlockAccessHelper<>(copyFrom.iterFunc, function -> copyFrom.andThen.accept(i -> function.apply(mapper.apply(i))), copyFrom.empty);
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

	public BlockAccessHelper(AbstractInputAccessHelper<Block, F> copyFrom) {
		this(copyFrom.iterFunc, copyFrom.andThen, copyFrom.empty);
	}

	public BlockAccessHelper(IterFunc<F> func, Consumer<Function<Block, F>> andThen, F empty) {
		super(func, andThen, empty);
		this.block = new FunctionAccessHelper<>(this);
		this.fluid = FunctionAccessHelper.create(this, AccessInternal::from);
		this.blockTag = new TaggedAccessHelper<>(this);
		this.fluidTag = TaggedAccessHelper.create(this, AccessInternal::from);
		this.blockRegistry = new RegistryAccessHelper<>(Registry.BLOCK, this);
		this.fluidRegistry = RegistryAccessHelper.create(Registry.FLUID, this, AccessInternal::from);
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

	public RegistryAccessHelper<Block, F> getBlockRegistry() {
		return this.blockRegistry;
	}

	public RegistryAccessHelper<Fluid, F> getFluidRegistry() {
		return this.fluidRegistry;
	}
}

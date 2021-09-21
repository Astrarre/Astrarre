package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.internal.AccessInternal;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.AbstractAccessHelper;
import io.github.astrarre.access.v0.api.helper.AccessHelpers;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.registry.Registry;

/**
 * Advanced filtering for Blocks
 */
public class BlockAccessHelper<F> extends AbstractAccessHelper<Block, F> {
	protected final RegistryAccessHelper<Block, F> blockRegistry;
	protected final RegistryAccessHelper<Fluid, F> fluidRegistry;
	protected final FunctionAccessHelper<Block, F> block;
	protected final FunctionAccessHelper<Fluid, F> fluid;
	protected final TaggedAccessHelper<Block, F> blockTag;
	protected final TaggedAccessHelper<Fluid, F> fluidTag;

	public BlockAccessHelper(AccessHelpers.Context<Block, F> copyFrom) {
		super(copyFrom);
		this.block = new FunctionAccessHelper<>(copyFrom);
		this.fluid = new FunctionAccessHelper<>(copyFrom.map(AccessInternal::from));
		this.blockTag = new TaggedAccessHelper<>(copyFrom);
		this.fluidTag = new TaggedAccessHelper<>(copyFrom.map(AccessInternal::from));
		this.blockRegistry = new RegistryAccessHelper<>(Registry.BLOCK, copyFrom);
		this.fluidRegistry = new RegistryAccessHelper<>(Registry.FLUID, copyFrom.map(AccessInternal::from));
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

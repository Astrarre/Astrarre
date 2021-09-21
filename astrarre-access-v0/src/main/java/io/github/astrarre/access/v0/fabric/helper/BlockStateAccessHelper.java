package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.AbstractAccessHelper;
import io.github.astrarre.access.v0.api.helper.AccessHelpers;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;

public class BlockStateAccessHelper<F> extends AbstractAccessHelper<BlockState, F> {
	protected final FunctionAccessHelper<BlockState, F> blockstate;
	protected final BlockAccessHelper<F> block;

	public BlockStateAccessHelper(AccessHelpers.Context<BlockState, F> copyFrom) {
		super(copyFrom);
		this.blockstate = new FunctionAccessHelper<>(copyFrom);
		this.block = new BlockAccessHelper<>(copyFrom.map(AbstractBlock.AbstractBlockState::getBlock));
	}


	public FunctionAccessHelper<BlockState, F> getBlockstate() {
		return this.blockstate;
	}

	public BlockAccessHelper<F> getBlock() {
		return this.block;
	}
}

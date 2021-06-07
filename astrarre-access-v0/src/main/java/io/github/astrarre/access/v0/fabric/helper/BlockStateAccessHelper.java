package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;

public class BlockStateAccessHelper<F> {
	protected final FunctionAccessHelper<BlockState, BlockState, F> blockstate;
	protected final BlockAccessHelper<BlockState, F> block;

	public BlockStateAccessHelper(IterFunc<F> func, Consumer<Function<BlockState, F>> adder) {
		this(func, adder, null);
	}

	public BlockStateAccessHelper(IterFunc<F> func, Consumer<Function<BlockState, F>> adder, F empty) {
		this.blockstate = new FunctionAccessHelper<>(func, adder, Function.identity(), empty);
		this.block = new BlockAccessHelper<>(func, adder, AbstractBlock.AbstractBlockState::getBlock, empty);
	}

	public FunctionAccessHelper<BlockState, BlockState, F> getBlockstate() {
		return this.blockstate;
	}

	public BlockAccessHelper<BlockState, F> getBlock() {
		return this.block;
	}
}

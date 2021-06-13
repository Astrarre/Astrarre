package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;

public class BlockStateAccessHelper<F> {
	protected final FunctionAccessHelper<BlockState, F> blockstate;
	protected final BlockAccessHelper<F> block;

	public BlockStateAccessHelper(Access<F> func, Function<Function<BlockState, F>, F> adder, F empty) {
		this(func.combiner, f -> func.andThen(adder.apply(f)), empty);
	}

	public BlockStateAccessHelper(Access<F> func, Function<Function<BlockState, F>, F> adder) {
		this(func, adder, null);
	}

	public BlockStateAccessHelper(IterFunc<F> func, Consumer<Function<BlockState, F>> adder) {
		this(func, adder, null);
	}

	public BlockStateAccessHelper(IterFunc<F> func, Consumer<Function<BlockState, F>> adder, F empty) {
		this.blockstate = new FunctionAccessHelper<>(func, adder, empty);
		this.block = BlockAccessHelper.create(func, adder, AbstractBlock.AbstractBlockState::getBlock, empty);
	}

	public FunctionAccessHelper<BlockState, F> getBlockstate() {
		return this.blockstate;
	}

	public BlockAccessHelper<F> getBlock() {
		return this.block;
	}
}

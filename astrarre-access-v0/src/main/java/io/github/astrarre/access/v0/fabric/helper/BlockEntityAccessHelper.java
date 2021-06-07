package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

/**
 * Advanced filtering for BlockEntity
 */
public class BlockEntityAccessHelper<F> {
	protected final FunctionAccessHelper<BlockEntity, BlockEntity, F> blockEntity;
	protected final FunctionAccessHelper<BlockEntity, BlockEntityType<?>, F> blockEntityType;
	protected final TaggedAccessHelper<BlockEntity, BlockEntityType<?>, F> blockEntityTag;

	public BlockEntityAccessHelper(IterFunc<F> func, Consumer<Function<BlockEntity, F>> adder) {
		this(func, adder, null);
	}

	public BlockEntityAccessHelper(IterFunc<F> func, Consumer<Function<BlockEntity, F>> adder, F empty) {
		this.blockEntity = new FunctionAccessHelper<>(func, adder, Function.identity(), empty);
		this.blockEntityType = new FunctionAccessHelper<>(func, adder, BlockEntity::getType, empty);
		this.blockEntityTag = new TaggedAccessHelper<>(func, adder, BlockEntity::getType, empty);
	}

	public FunctionAccessHelper<BlockEntity, BlockEntity, F> getBlockEntity() {
		return this.blockEntity;
	}

	public FunctionAccessHelper<BlockEntity, BlockEntityType<?>, F> getBlockEntityType() {
		return this.blockEntityType;
	}

	public TaggedAccessHelper<BlockEntity, BlockEntityType<?>, F> getBlockEntityTag() {
		return this.blockEntityTag;
	}
}

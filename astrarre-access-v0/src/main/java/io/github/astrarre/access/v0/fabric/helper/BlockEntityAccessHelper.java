package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;

/**
 * Advanced filtering for BlockEntity
 */
public class BlockEntityAccessHelper<F> {
	protected final FunctionAccessHelper<BlockEntity, F> blockEntity;
	protected final FunctionAccessHelper<BlockEntityType<?>, F> blockEntityType;
	protected final TaggedAccessHelper<BlockEntityType<?>, F> blockEntityTag;

	public BlockEntityAccessHelper(Access<F> func, Function<Function<BlockEntity, F>, F> adder, F empty) {
		this(func.combiner, f -> func.andThen(adder.apply(f)), empty);
	}

	public BlockEntityAccessHelper(Access<F> func, Function<Function<BlockEntity, F>, F> adder) {
		this(func, adder, null);
	}

	public BlockEntityAccessHelper(IterFunc<F> func, Consumer<Function<BlockEntity, F>> adder) {
		this(func, adder, null);
	}

	public BlockEntityAccessHelper(IterFunc<F> func, Consumer<Function<BlockEntity, F>> adder, F empty) {
		this.blockEntity = new FunctionAccessHelper<>(func, adder, empty);
		this.blockEntityType = FunctionAccessHelper.create(func, adder, BlockEntity::getType, empty);
		this.blockEntityTag = TaggedAccessHelper.create(func, adder, BlockEntity::getType, empty);
	}

	public FunctionAccessHelper<BlockEntity, F> getBlockEntity() {
		return this.blockEntity;
	}

	public FunctionAccessHelper<BlockEntityType<?>, F> getBlockEntityType() {
		return this.blockEntityType;
	}

	public TaggedAccessHelper<BlockEntityType<?>, F> getBlockEntityTag() {
		return this.blockEntityTag;
	}
}

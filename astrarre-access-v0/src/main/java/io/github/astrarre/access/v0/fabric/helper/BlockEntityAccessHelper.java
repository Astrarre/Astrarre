package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.AbstractInputAccessHelper;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.util.registry.Registry;

/**
 * Advanced filtering for BlockEntity
 */
public class BlockEntityAccessHelper<F> extends AbstractInputAccessHelper<BlockEntity, F> {
	protected final FunctionAccessHelper<BlockEntity, F> blockEntity;
	protected final FunctionAccessHelper<BlockEntityType<?>, F> blockEntityType;
	protected final TaggedAccessHelper<BlockEntityType<?>, F> blockEntityTag;
	protected final RegistryAccessHelper<BlockEntityType<?>, F> blockEntityTypeRegistry;

	public BlockEntityAccessHelper(AbstractInputAccessHelper<BlockEntity, F> copyFrom) {
		this(copyFrom.iterFunc, copyFrom.andThen, copyFrom.empty);
	}

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
		super(func, adder, empty);
		this.blockEntity = new FunctionAccessHelper<>(this);
		this.blockEntityType = FunctionAccessHelper.create(this, BlockEntity::getType);
		this.blockEntityTag = TaggedAccessHelper.create(this, BlockEntity::getType);
		this.blockEntityTypeRegistry = RegistryAccessHelper.create(Registry.BLOCK_ENTITY_TYPE, this, BlockEntity::getType);
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

	public RegistryAccessHelper<BlockEntityType<?>, F> getBlockEntityTypeRegistry() {
		return this.blockEntityTypeRegistry;
	}
}

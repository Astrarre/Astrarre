package io.github.astrarre.access.v0.fabric.helper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.helper.AbstractAccessHelper;
import io.github.astrarre.access.v0.api.helper.AccessHelpers;
import io.github.astrarre.access.v0.api.helper.FunctionAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

/**
 * Advanced filtering for BlockEntity
 */
public class BlockEntityAccessHelper<F> extends AbstractAccessHelper<BlockEntity, F> {
	protected final FunctionAccessHelper<BlockEntity, F> blockEntity;
	protected final FunctionAccessHelper<BlockEntityType<?>, F> blockEntityType;
	protected final TaggedAccessHelper<BlockEntityType<?>, F> blockEntityTag;
	protected final RegistryAccessHelper<BlockEntityType<?>, F> blockEntityTypeRegistry;

	public BlockEntityAccessHelper(AccessHelpers.Context<BlockEntity, F> copyFrom) {
		super(copyFrom);
		this.blockEntity = new FunctionAccessHelper<>(copyFrom);
		this.blockEntityType = new FunctionAccessHelper<>(copyFrom.map(BlockEntity::getType));
		this.blockEntityTag = new TaggedAccessHelper<>(copyFrom.map(BlockEntity::getType));
		this.blockEntityTypeRegistry = new RegistryAccessHelper<>(Registry.BLOCK_ENTITY_TYPE, copyFrom.map(BlockEntity::getType));
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

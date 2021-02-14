package io.github.astrarre.access.v0.api.cache;

import java.lang.ref.WeakReference;

import io.github.astrarre.access.internal.access.WorldChunkAccess;
import io.github.astrarre.access.internal.access.BlockEntityAccess;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.func.WorldFunction;
import io.github.astrarre.v0.block.BlockState;
import io.github.astrarre.v0.block.entity.BlockEntity;
import io.github.astrarre.v0.util.math.BlockPos;
import io.github.astrarre.v0.util.math.Direction;
import io.github.astrarre.v0.world.World;

import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;

public final class CachedWorldQuery {
	static {
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((entity, world1) -> ((BlockEntityAccess)entity).astrarre_invalidate());
		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((entity, world1) -> ((BlockEntityAccess)entity).astrarre_invalidate());
	}

	protected final BlockPos pos;
	protected final World world;
	protected BlockState state;
	protected WeakReference<BlockEntity> reference;

	public static CachedWorldQuery getOrCreate(BlockPos pos, World world) {
		WorldChunk chunk = ((net.minecraft.world.World)world).getWorldChunk((net.minecraft.util.math.BlockPos) pos);
		return ((WorldChunkAccess)chunk).astrarre_getOrCreate((net.minecraft.util.math.BlockPos) pos, ((world1, pos1) -> {
			net.minecraft.block.BlockState state = chunk.getBlockState(pos1);
			net.minecraft.block.entity.BlockEntity entity = null;
			if(state.getBlock().hasBlockEntity()) {
				entity = chunk.getBlockEntity(pos1);
			}
			return new CachedWorldQuery(world1, pos1, state, entity);
		}));
	}

	private CachedWorldQuery(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos) {
		this.pos = (BlockPos) pos;
		this.world = (World) world;
	}

	private CachedWorldQuery(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state, net.minecraft.block.entity.BlockEntity entity) {
		this(world, pos);
		this.state = (BlockState) state;
		if(entity != null) {
			this.reference = new WeakReference<>((BlockEntity) entity);
		}
	}

	public void invalidateBlockState() {
		this.state = null;
	}

	public void invalidateBlockEntity() {
		this.reference = null;
	}

	public <T> T get(Access<WorldFunction<T>, T> access, Direction direction) {
		return access.get().get(direction, this.getBlockState(), this.world, this.pos, this.getBlockEntity());
	}

	public BlockState getBlockState() {
		if(this.state == null) {
			return this.state = this.world.getBlockState(this.pos);
		}
		return this.state;
	}

	public BlockEntity getBlockEntity() {
		if((this.reference == null || this.reference.get() == null) && this.getBlockState().getBlock().hasBlockEntity()) {
			BlockEntity entity = this.world.getBlockEntity(this.pos);
			if(entity != null) {
				this.reference = new WeakReference<>(entity);
				((BlockEntityAccess)entity).astrarre_addRemoveOrMoveListener(e -> this.invalidateBlockEntity());
			}
			return entity;
		}

		return this.reference != null ? this.reference.get() : null;
	}
}

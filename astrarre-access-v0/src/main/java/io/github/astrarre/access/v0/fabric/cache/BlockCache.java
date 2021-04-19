package io.github.astrarre.access.v0.fabric.cache;

import java.lang.ref.WeakReference;

import io.github.astrarre.access.internal.access.WorldChunkAccess;
import io.github.astrarre.access.internal.access.BlockEntityAccess;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.fabric.func.WorldFunction;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

/**
 * cache a reference to a block & it's block entity, it automatically updates
 */
public final class BlockCache {
	protected final BlockPos pos;
	protected final World world;
	protected BlockState state;
	protected WeakReference<BlockEntity> reference; // weak reference just in case

	public static BlockCache getOrCreate(BlockPos pos, World world) {
		WorldChunk chunk = world.getWorldChunk(pos);
		return ((WorldChunkAccess)chunk).astrarre_getOrCreate(pos, ((world1, pos1) -> {
			BlockState state = chunk.getBlockState(pos1);
			BlockEntity entity = null;
			if(state.getBlock().hasBlockEntity()) {
				entity = chunk.getBlockEntity(pos1);
			}
			return new BlockCache(world1, pos1, state, entity);
		}));
	}

	private BlockCache(World world, BlockPos pos) {
		this.pos = pos;
		this.world = world;
	}

	private BlockCache(World world, BlockPos pos, BlockState state, BlockEntity entity) {
		this(world, pos);
		this.state = state;
		if(entity != null) {
			this.reference = new WeakReference<>(entity);
		}
	}

	public void invalidateBlockState() {
		this.state = null;
	}

	public void invalidateBlockEntity() {
		this.reference = null;
	}

	public <T> T get(Access<WorldFunction<T>> access, Direction direction) {
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

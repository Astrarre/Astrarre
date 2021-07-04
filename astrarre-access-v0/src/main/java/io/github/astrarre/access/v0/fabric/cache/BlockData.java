package io.github.astrarre.access.v0.fabric.cache;

import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

/**
 * A temporary version of {@link BlockCache}, all references are not auto-invalidated.
 */
public final class BlockData {
	protected final World world;
	protected final BlockPos pos;
	protected WorldChunk chunk;
	protected BlockState state;
	protected BlockEntity entity;

	public BlockData(@NotNull BlockEntity entity) {
		this(Validate.notNull(entity.getWorld(), "world == null!"), null, entity.getPos(), entity.getCachedState(), entity);
	}

	public BlockData(@NotNull World world,
			@Nullable WorldChunk chunk,
			@NotNull BlockPos pos,
			@Nullable BlockState state,
			@Nullable BlockEntity entity) {
		this.world = Validate.notNull(world, "world == null!");
		this.chunk = chunk;
		this.pos = Validate.notNull(pos, "pos == null!");
		this.state = state;
		this.entity = entity;
	}

	public BlockData(@NotNull World world, @NotNull BlockPos pos) {
		this(world, null, pos, null, null);
	}

	public BlockData(@NotNull World world, @NotNull BlockPos pos, @Nullable BlockState state) {
		this(world, null, pos, state, null);
	}

	public BlockData(@NotNull WorldChunk chunk, @NotNull BlockPos pos) {
		this(chunk.getWorld(), chunk, pos, null, null);
	}

	public BlockData(@NotNull WorldChunk chunk, @NotNull BlockPos pos, @Nullable BlockState state) {
		this(chunk.getWorld(), chunk, pos, state, null);
	}

	public BlockData(@Nullable WorldChunk chunk, @NotNull BlockEntity entity) {
		this(Validate.notNull(entity.getWorld(), "world == null!"), chunk, entity.getPos(), entity.getCachedState(), entity);
	}

	public BlockState getState() {
		BlockState state = this.state;
		if(this.state == null) {
			BlockEntity entity = this.getEntity();
			if(entity == null) {
				this.state = state = this.getChunk().getBlockState(this.pos);
			} else {
				this.state = state = entity.getCachedState();
			}
		}
		return state;
	}

	public WorldChunk getChunk() {
		WorldChunk chunk = this.chunk;
		if(chunk == null) {
			this.chunk = chunk = this.world.getWorldChunk(this.pos);
		}
		return chunk;
	}

	public BlockEntity getEntity() {
		BlockEntity entity = this.entity;
		if(entity == null) {
			BlockState state = this.state;
			if(state == null || state.hasBlockEntity()) {
				entity = this.getChunk().getBlockEntity(this.pos);
			}

			if(entity != null && state == null) {
				this.state = entity.getCachedState();
			}
		}
		return entity;
	}

	public World getWorld() {
		return this.world;
	}

	public BlockPos getPos() {
		return this.pos;
	}
}
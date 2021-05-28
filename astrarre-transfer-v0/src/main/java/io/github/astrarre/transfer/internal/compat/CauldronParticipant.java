package io.github.astrarre.transfer.internal.compat;

import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.LavaCauldronBlock;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CauldronParticipant implements Participant<Fluid> {
	protected final World world;
	protected final BlockPos pos;
	protected final Key key;

	public CauldronParticipant(BlockState state, World world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
		this.key = new Key(state);
	}

	@Override
	public void extract(@Nullable Transaction transaction, Insertable<Fluid> insertable) {
		BlockState state = this.key.getRootValue();
		int level = this.getLevel(state);
		if(level == -1 || level == 0) {
			return;
		}

		try(Transaction action = Transaction.create()) {
			Fluid from = this.getFluid(state);
			int insert = insertable.insert(action, from, level * Droplet.BOTTLE);
			if(insert % this.unit(from) != 0) {
				action.abort();
				return;
			}

			int subtracted = insert / Droplet.BOTTLE;
			this.key.set(action, this.from(from, level - subtracted));
		}
	}

	protected BlockState from(Fluid fluid, int level) {
		if(fluid == Fluids.LAVA) {
			return Blocks.LAVA_CAULDRON.getDefaultState();
		} else {
			return Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, level);
		}
	}

	protected Fluid getFluid(BlockState state) {
		Block block = state.getBlock();
		if(block instanceof LavaCauldronBlock) {
			return Fluids.LAVA;
		} else if(block instanceof LeveledCauldronBlock) {
			return Fluids.WATER;
		}
		return Fluids.EMPTY;
	}

	protected int unit(Fluid fluid) {
		if(fluid == Fluids.WATER) {
			return Droplet.BOTTLE;
		} else {
			return Droplet.BUCKET;
		}
	}

	protected int getLevel(BlockState state) {
		Block block = state.getBlock();
		if(block instanceof LavaCauldronBlock) {
			return 3;
		} else if(block instanceof LeveledCauldronBlock) {
			return state.get(LeveledCauldronBlock.LEVEL);
		} else if(block instanceof CauldronBlock) {
			return 0;
		}
		return -1;
	}

	@Override
	public int insert(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
		BlockState state = this.key.getRootValue();
		int level = this.getLevel(state);
		if(level == -1) return 0;
		Fluid fluid = this.getFluid(state);
		if(type == fluid || fluid == Fluids.EMPTY) {
			int deltaLevel = Math.min(3 - level, quantity / Droplet.BOTTLE);
			if(deltaLevel == 0 || (deltaLevel + level) % (this.unit(fluid) / Droplet.BOTTLE) != 0) {
				return 0;
			}
			this.key.set(transaction, this.from(fluid, deltaLevel + level));
			return deltaLevel * Droplet.BOTTLE;
		}
		return 0;
	}

	public class Key extends ObjectKeyImpl<BlockState> {
		public Key(BlockState originalValue) {
			super(originalValue);
		}

		@Override
		protected BlockState getRootValue() {
			return CauldronParticipant.this.world.getBlockState(CauldronParticipant.this.pos);
		}

		@Override
		protected void setRootValue(BlockState val) {
			super.setRootValue(val);
			CauldronParticipant.this.world.updateNeighbors(CauldronParticipant.this.pos, val.getBlock());
		}

		@Override
		public void onAbort(Transaction transaction) {
			super.onAbort(transaction);
			CauldronParticipant.this.world.setBlockState(CauldronParticipant.this.pos, this.values.top(), 4);
		}

		@Override
		public void set(Transaction transaction, BlockState val) {
			super.set(transaction, val);
			if (transaction == null) {
				CauldronParticipant.this.world.setBlockState(CauldronParticipant.this.pos, val);
			} else {
				CauldronParticipant.this.world.setBlockState(CauldronParticipant.this.pos, val, 4);
			}
		}
	}
}

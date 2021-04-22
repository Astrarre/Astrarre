package io.github.astrarre.transfer.internal.compat;

import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Extractable;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CauldronParticipant implements Participant<Fluid>, Extractable.Simple<Fluid> {
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
		if(state.getBlock() != Blocks.CAULDRON) {
			return;
		}

		try(Transaction action = Transaction.create()) {
			int level = state.get(CauldronBlock.LEVEL);
			int insert = insertable.insert(action, Fluids.WATER, level * Droplet.BOTTLE);
			if(insert % Droplet.BOTTLE != 0) {
				action.abort();
				return;
			}

			int subtracted = insert / Droplet.BOTTLE;
			this.key.set(action, state.with(CauldronBlock.LEVEL, level - subtracted));
		}
	}

	@Override
	public int insert(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
		BlockState state = this.key.getRootValue();
		if(state.getBlock() != Blocks.CAULDRON) {
			return 0;
		}

		int level = state.get(CauldronBlock.LEVEL);
		if(type == Fluids.WATER) {
			int deltaLevel = Math.min(3 - level, quantity / Droplet.BOTTLE);
			if(deltaLevel == 0) {
				return 0;
			}
			this.key.set(transaction, state.with(CauldronBlock.LEVEL, deltaLevel + level));
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

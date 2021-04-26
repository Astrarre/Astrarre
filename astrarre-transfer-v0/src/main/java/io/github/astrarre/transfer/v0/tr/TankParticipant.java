package io.github.astrarre.transfer.v0.tr;

import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.participants.array.Slot;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import io.github.astrarre.transfer.v0.api.transaction.keys.generated.IntKeyImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reborncore.common.fluid.FluidValue;
import reborncore.common.util.Tank;

import net.minecraft.fluid.Fluid;

public class TankParticipant implements Slot<Fluid> {
	public final Tank tank;
	/**
	 * stores the amount of fluid in the tank in mb
	 */
	public final TankQuantityKey mb = new TankQuantityKey();
	public final TankFluidKey fluid = new TankFluidKey();
	public TankParticipant(Tank tank) {
		this.tank = tank;
	}

	@Override
	public Fluid getKey(@Nullable Transaction transaction) {
		return this.fluid.get(transaction);
	}

	@Override
	public int getQuantity(@Nullable Transaction transaction) {
		return Droplet.fromMb(this.mb.get(transaction));
	}

	@Override
	public int insert(@Nullable Transaction transaction, @NotNull Fluid key, int quantity) {
		return Slot.super.insert(transaction, key, Droplet.fromMb(Math.min(this.tank.getCapacity().getRawValue() - this.mb.get(transaction), Math.floorDiv(quantity, 81))));
	}

	@Override
	public boolean set(@Nullable Transaction transaction, Fluid key, int quantity) {
		if(quantity % 81 == 0) {
			int mbQuantity = quantity / 81;
			if(!this.tank.getCapacity().equalOrMoreThan(FluidValue.fromRaw(mbQuantity))) {
				this.fluid.set(transaction, key);
				this.mb.set(transaction, mbQuantity);
			}
		}
		return false;
	}

	public class TankQuantityKey extends IntKeyImpl {
		@Override
		protected int getRootValue() {
			return TankParticipant.this.tank.getFluidAmount().getRawValue();
		}

		@Override
		protected void setRootValue(int val) {
			TankParticipant.this.tank.setFluidAmount(FluidValue.fromRaw(val));
		}
	}

	public class TankFluidKey extends ObjectKeyImpl<Fluid> {
		@Override
		protected Fluid getRootValue() {
			return TankParticipant.this.tank.getFluid();
		}

		@Override
		protected void setRootValue(Fluid val) {
			TankParticipant.this.tank.setFluid(val);
		}
	}
}

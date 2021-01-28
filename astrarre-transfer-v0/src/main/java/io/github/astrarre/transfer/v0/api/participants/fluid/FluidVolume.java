package io.github.astrarre.transfer.v0.api.participants.fluid;

import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Key;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import io.github.astrarre.transfer.v0.api.transaction.keys.generated.IntKeyImpl;
import io.github.astrarre.v0.fluid.Fluid;
import io.github.astrarre.v0.fluid.Fluids;
import io.github.astrarre.v0.util.registry.Registry;

// todo document terminology, FluidVolume, FluidContainer, etc.
public class FluidVolume implements Participant<Fluid> {
	protected final Key.Object<Fluid> fluid;
	protected final Key.Int quantity;

	public FluidVolume(Fluid fluid, int quantity) {
		if (fluid == Fluids.EMPTY && quantity != 0) {
			throw new IllegalArgumentException("cannot have " + quantity + " units of EMPTY!");
		} else if (quantity < 0) {
			throw new IllegalArgumentException("Cannot have negative units of " + Registry.FLUID.getId(fluid));
		}

		if (quantity == 0) {
			fluid = Fluids.EMPTY;
		}

		this.fluid = new ObjectKeyImpl<>(fluid);
		this.quantity = new IntKeyImpl(quantity);
	}

	@Override
	public int insert(Transaction transaction, Fluid type, int amount) {
		if (amount == 0) {
			return 0;
		}

		Fluid fluid = this.fluid.get(transaction);
		if (fluid == Fluids.EMPTY || fluid == type) {
			if (fluid != type) {
				this.fluid.set(transaction, type);
			}

			this.quantity.set(transaction, this.quantity.get(transaction) + amount);
			return amount;
		}
		return 0;
	}

	@Override
	public void extract(Transaction transaction, Insertable<Fluid> insertable) {
		int oldLevel = this.quantity.get(transaction);
		int amount = insertable.insert(transaction, this.fluid.get(transaction), oldLevel);
		int newLevel = oldLevel - amount;
		this.quantity.set(transaction, newLevel);
		if (newLevel == 0) {
			this.fluid.set(transaction, Fluids.EMPTY);
		}
	}

	@Override
	public int extract(Transaction transaction, Fluid type, int amount) {
		if (amount == 0) {
			return 0;
		}

		if (this.fluid.get(transaction) == type) {
			int oldLevel = this.quantity.get(transaction);
			int toExtract = Math.min(oldLevel, amount);
			int newLevel = oldLevel - toExtract;
			if (newLevel == 0) {
				this.fluid.set(transaction, Fluids.EMPTY);
			}
			return toExtract;
		}

		return 0;
	}

	@Override
	public boolean isEmpty(Transaction transaction) {
		return this.fluid.get(transaction) == Fluids.EMPTY;
	}

	
}

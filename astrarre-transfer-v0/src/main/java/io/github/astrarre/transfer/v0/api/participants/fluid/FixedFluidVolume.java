package io.github.astrarre.transfer.v0.api.participants.fluid;

import io.github.astrarre.transfer.v0.api.transaction.Transaction;

import net.minecraft.fluid.Fluid;

public class FixedFluidVolume extends FluidVolume {
	protected final int max;
	public FixedFluidVolume(Fluid fluid, int quantity, int max) {
		super(fluid, quantity);
		this.max = max;
	}


	@Override
	public int insert(Transaction transaction, Fluid type, int amount) {
		return super.insert(transaction, type, Math.min(this.getMax() - this.quantity.get(transaction), amount));
	}

	/**
	 * @return if the maximum size of the container is dynamic, this can be overriden
	 */
	public int getMax() {
		return this.max;
	}
}

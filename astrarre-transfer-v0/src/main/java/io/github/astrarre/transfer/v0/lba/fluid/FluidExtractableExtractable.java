package io.github.astrarre.transfer.v0.lba.fluid;

import java.math.RoundingMode;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.ExactFluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Extractable;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import io.github.astrarre.transfer.v0.api.util.Quantity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public class FluidExtractableExtractable implements Extractable<Fluid> {
	protected final FluidExtractable extractable;
	protected final FluidInsertableKey toExtract;

	public FluidExtractableExtractable(FluidExtractable extractable) {
		this.extractable = extractable;
		this.toExtract = new FluidInsertableKey();
	}

	@Override
	public void extract(@Nullable Transaction transaction, Insertable<Fluid> insertable) {
		Quantity<Fluid> current = this.toExtract.get(transaction);
		if(current.isEmpty()) {
			int[] validFound = {0};
			FluidVolume toExtract = this.extractable.attemptExtraction(fluidKey -> {
				if(fluidKey.getRawFluid() == null) return false;

				try(Transaction transaction1 = Transaction.create(false)) {
					int c = insertable.insert(transaction1, fluidKey.getRawFluid(), 1);
					if(c > 0) {
						validFound[0] = c;
						return true;
					}
					return false;
				}
			}, FluidAmount.of(1000, Droplet.BUCKET), Simulation.SIMULATE);

			if(validFound[0] == 0 || toExtract.isEmpty()) {
				return;
			}

			FluidVolume verify = this.extractable.attemptExtraction(new ExactFluidFilter(toExtract.fluidKey), validFound[0], Simulation.SIMULATE);
			long denom = verify.getAmount_F().denominator;
			if(Droplet.BUCKET % denom != 0) {
				return;
			}
			if(!verify.isEmpty() && verify.getRawFluid() != null && verify.getRawFluid() != Fluids.EMPTY) {
				try(Transaction transaction1 = Transaction.create()) {
					int count = verify.getAmount_F().asInt(81000, RoundingMode.FLOOR);
					if(insertable.insert(transaction, LBAFluidsCompat.of(verify).type, count) != count) {
						transaction1.abort();
						return;
					}
				}
				this.toExtract.set(transaction, LBAFluidsCompat.of(verify));
			}
		}
	}

	protected class FluidInsertableKey extends ObjectKeyImpl<Quantity<Fluid>> {
		@Override
		protected Quantity<Fluid> getRootValue() {
			return Quantity.EMPTY_FLUID;
		}

		@Override
		protected void setRootValue(Quantity<Fluid> val) {
			FluidVolume volume = LBAFluidsCompat.of(val);
			FluidExtractableExtractable.this.extractable.extract(volume.fluidKey, volume.getAmount_F());
		}
	}
}

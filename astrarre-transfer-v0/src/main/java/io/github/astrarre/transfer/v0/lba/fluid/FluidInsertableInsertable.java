package io.github.astrarre.transfer.v0.lba.fluid;

import java.math.RoundingMode;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.transaction.keys.ObjectKeyImpl;
import io.github.astrarre.transfer.v0.api.util.Quantity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;

public class FluidInsertableInsertable implements Insertable<Fluid> {
	protected final FluidInsertable insertable;
	protected final FluidInsertableKey key;
	protected final boolean aware;

	/**
	 * @see FluidExtractableExtractable
	 */
	public FluidInsertableInsertable(FluidInsertable insertable, boolean aware) {
		this.insertable = insertable;
		this.key = new FluidInsertableKey();
		this.aware = aware;
	}

	@Override
	public int insert(@Nullable Transaction transaction, @NotNull Fluid type, int quantity) {
		Quantity<Fluid> current = this.key.get(transaction);
		if(current.isEmpty() || current.isTypeEqual(type)) {
			FluidVolume success = FluidKeys.get(type).withAmount(FluidAmount.of(quantity + current.amount, Droplet.BUCKET));
			FluidVolume remainder = this.insertable.attemptInsertion(success, Simulation.SIMULATE);
			long denom = remainder.getAmount_F().denominator;
			if(Droplet.BUCKET % denom != 0) {
				return 0;
			}
			success = success.withAmount(success.getAmount_F().sub(remainder.getAmount_F()));
			this.key.set(transaction, LBAFluidsCompat.of(success));
			return success.getAmount_F().asInt(Droplet.BUCKET, RoundingMode.FLOOR) - current.amount;
		}
		return 0;
	}

	protected class FluidInsertableKey extends ObjectKeyImpl<Quantity<Fluid>> {
		@Override
		protected Quantity<Fluid> getRootValue() {
			return Quantity.EMPTY_FLUID;
		}

		@Override
		protected void setRootValue(Quantity<Fluid> val) {
			FluidInsertableInsertable.this.insertable.insert(LBAFluidsCompat.of(val));
		}
	}
}
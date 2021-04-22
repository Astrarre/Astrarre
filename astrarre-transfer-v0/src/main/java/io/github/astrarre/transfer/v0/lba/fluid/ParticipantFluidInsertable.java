package io.github.astrarre.transfer.v0.lba.fluid;

import java.math.RoundingMode;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;

public class ParticipantFluidInsertable implements FluidInsertable {

	public final Participant<Fluid> participant;

	public ParticipantFluidInsertable(Participant<Fluid> participant) {
		this.participant = participant;
	}

	@Override
	public FluidVolume attemptInsertion(FluidVolume fluid, Simulation simulation) {
		if(fluid.getRawFluid() == null) return FluidVolumeUtil.EMPTY;

		try(Transaction transaction = Transaction.create(simulation.isAction())) {
			FluidVolume copy = fluid.copy();
			int count = fluid.getAmount_F().asInt(Droplet.BUCKET, RoundingMode.FLOOR);
			int inserted = this.participant.insert(transaction, fluid.getRawFluid(), count);
			copy = copy.withAmount(copy.getAmount_F().sub(FluidAmount.of(inserted, Droplet.BUCKET)));
			return copy;
		}
	}
}

package io.github.astrarre.transfer.v0.lba.fluid;

import java.math.RoundingMode;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.ExactFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.item.ItemSlotParticipant;
import io.github.astrarre.transfer.v0.api.participants.FixedObjectVolume;
import io.github.astrarre.transfer.v0.api.participants.ObjectVolume;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.lba.item.ItemFilterFilteringInsertable;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public class ParticipantFluidExtractable implements FluidExtractable {
	public final Participant<Fluid> participant;

	public ParticipantFluidExtractable(Participant<Fluid> participant) {
		this.participant = participant;
	}

	@Override
	public FluidVolume attemptExtraction(FluidFilter filter, FluidAmount amount, Simulation simulation) {
		int quantity = amount.asInt(Droplet.BUCKET, RoundingMode.FLOOR);
		try (Transaction transaction = Transaction.create(simulation.isAction())) {
			if (filter instanceof ExactFluidFilter) {
				FluidKey fluid = ((ExactFluidFilter) filter).fluid;
				if (fluid.getRawFluid() != null) {
					return fluid.withAmount(FluidAmount.of(this.participant.extract(transaction, fluid.getRawFluid(), quantity), Droplet.BUCKET));
				} else {
					return FluidVolumeUtil.EMPTY;
				}
			}

			ObjectVolume<Fluid> participant = new FixedObjectVolume<>(Fluids.EMPTY, quantity);
			this.participant.extract(transaction, new FluidFilterFilteringInsertable(filter, participant));
			return FluidKeys.get(participant.getType(transaction)).withAmount(FluidAmount.of(participant.getQuantity(transaction), Droplet.BUCKET));
		}
	}
}

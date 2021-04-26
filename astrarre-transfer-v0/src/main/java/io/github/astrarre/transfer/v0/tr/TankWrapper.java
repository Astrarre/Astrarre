package io.github.astrarre.transfer.v0.tr;

import io.github.astrarre.transfer.v0.api.Participant;
import reborncore.common.fluid.FluidValue;
import reborncore.common.util.Tank;

import net.minecraft.fluid.Fluid;

/**
 * hardcoded mixined support, kinda conc and wont work with mods that use TR api for anything but FluidConfiguration but if someone wants to use TR fluid api
 * that's their fault
 */
public class TankWrapper extends Tank {
	public final Participant<Fluid> participant;
	public TankWrapper(Participant<Fluid> participant) {
		super(null, FluidValue.EMPTY, null);
		this.participant = participant;
	}
}

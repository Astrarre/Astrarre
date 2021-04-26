package io.github.astrarre.transfer.v0.tr;

import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;
import reborncore.common.util.Tank;

import net.minecraft.fluid.Fluid;

import net.fabricmc.api.ModInitializer;

public class RebornCoreCompat implements ModInitializer {
	public static final FunctionAccess<Participant<Fluid>, Tank> TO_TANK = new FunctionAccess<>(Id.create("astrarre-transfer-v0", "to_tank"));
	static {
		Validate.ifModPresent("reborncore", () -> TO_TANK.andThen(TankWrapper::new));
	}

	@Override
	public void onInitialize() {

	}
}

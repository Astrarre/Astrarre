package io.github.astrarre.access.internal;

import io.github.astrarre.access.internal.mixin.FluidBlockAccess;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public class AccessInternal {
	public static Fluid from(Block block) {
		if(block instanceof FluidBlockAccess) {
			return ((FluidBlockAccess) block).getFluid();
		} else {
			return Fluids.EMPTY;
		}
	}
}

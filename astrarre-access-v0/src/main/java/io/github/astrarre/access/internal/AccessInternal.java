package io.github.astrarre.access.internal;

import io.github.astrarre.access.internal.mixin.FluidBlockAccess;
import io.github.astrarre.access.v0.fabric.func.BaseWorldFunction;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public class AccessInternal {
	public static final BaseWorldFunction.Type[] BASE_WORLD_FUNCTION_TYPES = BaseWorldFunction.Type.values();
	public static Fluid from(Block block) {
		if(block instanceof FluidBlockAccess) {
			return ((FluidBlockAccess) block).getFluid();
		} else {
			return Fluids.EMPTY;
		}
	}
}

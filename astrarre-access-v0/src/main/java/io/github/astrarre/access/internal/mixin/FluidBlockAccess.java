package io.github.astrarre.access.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;

@Mixin (FluidBlock.class)
public interface FluidBlockAccess {
	@Accessor
	FlowableFluid getFluid();
}

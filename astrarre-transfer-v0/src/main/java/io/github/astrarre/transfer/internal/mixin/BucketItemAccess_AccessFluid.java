package io.github.astrarre.transfer.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;

@Mixin (BucketItem.class)
public interface BucketItemAccess_AccessFluid {
	@Accessor
	Fluid getFluid();
}

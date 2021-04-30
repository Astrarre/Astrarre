package io.github.astrarre.recipe.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.RequiredTagList;

@Mixin (FluidTags.class)
public interface FluidTagsAccess {
	@Accessor
	static RequiredTagList<Fluid> getREQUIRED_TAGS() { throw new UnsupportedOperationException(); }
}

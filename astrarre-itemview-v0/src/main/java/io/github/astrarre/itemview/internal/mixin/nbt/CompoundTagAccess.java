package io.github.astrarre.itemview.internal.mixin.nbt;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.io.AbstractTag;
import net.minecraft.util.io.CompoundTag;

@Mixin(CompoundTag.class)
public interface CompoundTagAccess {
	@Accessor Map<String, AbstractTag> getData();
}

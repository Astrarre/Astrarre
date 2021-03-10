package io.github.astrarre.recipes.internal.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.tag.SetTag;

@Mixin (SetTag.class)
public interface SetTagAccess {
	@Accessor
	<T> Set<T> getValueSet();
}

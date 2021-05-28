package io.github.astrarre.util.internal.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.Lazy;

@Mixin (Lazy.class)
public interface LazyAccess<T> {
	@Accessor
	void setSupplier(Supplier<T> value);
}

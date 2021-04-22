package io.github.astrarre.util.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.tag.Tag;

@Mixin (targets = "net.minecraft.tag.RequiredTagList$TagWrapper")
public interface TagWrapperAccess {
	@Invoker
	<T> Tag<T> callGet();
}

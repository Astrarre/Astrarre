package io.github.astrarre.recipes.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.tag.Tag;

import net.fabricmc.fabric.impl.tag.extension.TagDelegate;

@Mixin (TagDelegate.class)
public interface TagDelegateAccess {
	@Invoker
	<T> Tag<T> callGetTag();
}

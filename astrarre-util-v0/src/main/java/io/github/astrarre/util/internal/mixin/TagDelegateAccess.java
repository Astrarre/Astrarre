package io.github.astrarre.util.internal.mixin;

import io.github.astrarre.util.v0.fabric.ModEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.tag.Tag;

import net.fabricmc.fabric.impl.tag.extension.TagDelegate;

@ModEnvironment ("fabric-tag-extensions-v0")
@Mixin (value = TagDelegate.class, remap = false)
public interface TagDelegateAccess {
	@Invoker
	<T> Tag<T> callGetTag();
}

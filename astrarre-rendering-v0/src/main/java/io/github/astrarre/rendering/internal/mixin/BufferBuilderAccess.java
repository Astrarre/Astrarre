package io.github.astrarre.rendering.internal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;

@Mixin (BufferBuilder.class)
public interface BufferBuilderAccess {
	@Accessor
	VertexFormat getFormat();
}

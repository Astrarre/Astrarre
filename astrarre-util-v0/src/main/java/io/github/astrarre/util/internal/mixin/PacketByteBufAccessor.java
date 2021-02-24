package io.github.astrarre.util.internal.mixin;

import io.netty.buffer.ByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.PacketByteBuf;

@Mixin (PacketByteBuf.class)
public interface PacketByteBufAccessor {
	@Mutable
	@Accessor
	void setParent(ByteBuf parent);
}

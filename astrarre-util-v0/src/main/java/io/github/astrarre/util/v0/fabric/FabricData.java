package io.github.astrarre.util.v0.fabric;

import java.util.UUID;
import java.util.function.Function;

import io.github.astrarre.util.internal.mixin.PacketByteBufAccessor;
import io.netty.buffer.ByteBuf;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public class FabricData {
	private static final ThreadLocal<PacketByteBuf> BUFFERS = ThreadLocal.withInitial(() -> new PacketByteBuf(null));

	public static ItemStack readStack(ByteBuf buf) {
		return read(buf, PacketByteBuf::readItemStack);
	}

	public static CompoundTag readTag(ByteBuf buf) {
		return read(buf, PacketByteBuf::readCompoundTag);
	}

	public static UUID readUUID(ByteBuf buf) {
		return read(buf, PacketByteBuf::readUuid);
	}

	public static <T> T read(ByteBuf buf, Function<PacketByteBuf, T> function) {
		if(buf instanceof PacketByteBuf) {
			return function.apply((PacketByteBuf) buf);
		} else {
			PacketByteBuf packetBuf = BUFFERS.get();
			((PacketByteBufAccessor)packetBuf).setParent(buf);
			T val = function.apply(packetBuf);
			((PacketByteBufAccessor)packetBuf).setParent(null);
			return val;
		}
	}
}

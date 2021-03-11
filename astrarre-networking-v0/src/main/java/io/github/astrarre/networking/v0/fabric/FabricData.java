package io.github.astrarre.networking.v0.fabric;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Function;

import io.github.astrarre.networking.internal.ByteBufDataInput;
import io.github.astrarre.networking.internal.ByteBufDataOutput;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.util.internal.mixin.PacketByteBufAccessor;
import io.github.astrarre.util.v0.api.Validate;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class FabricData {
	private static final ThreadLocal<PacketByteBuf> BUFFERS = ThreadLocal.withInitial(() -> new PacketByteBuf(null));
	private static final ThreadLocal<byte[]> BYTE_BUFFERS = ThreadLocal.withInitial(() -> new byte[2048]);

	public static ItemStack readStack(Input buf) {
		return read(buf, PacketByteBuf::readItemStack);
	}
	public static CompoundTag readTag(Input buf) {
		return read(buf, PacketByteBuf::readCompoundTag);
	}
	public static UUID readUUID(Input buf) {
		return read(buf, PacketByteBuf::readUuid);
	}
	public static BlockPos readPos(Input buf) {return read(buf, PacketByteBuf::readBlockPos);}

	public static PacketByteBuf from(Output output) {
		if(output instanceof ByteBufDataOutput) {
			return ((ByteBufDataOutput) output).buf;
		} else {
			throw new IllegalArgumentException("Output is not instance of " + ByteBufDataOutput.class);
		}
	}

	public static <T> T read(Input buf, Function<PacketByteBuf, T> function) {
		if(buf instanceof ByteBufDataInput) {
			return function.apply(((ByteBufDataInput) buf).buf);
		} else {
			int inp = buf.bytes();
			ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer(inp));
			byte[] bytes = BYTE_BUFFERS.get();
			try {
				buf.writeTo(bbos, bytes);
			} catch (IOException e) {
				throw Validate.rethrow(e);
			}

			PacketByteBuf packetBuf = BUFFERS.get();
			((PacketByteBufAccessor)packetBuf).setParent(bbos.buffer());
			T val = function.apply(packetBuf);
			((PacketByteBufAccessor)packetBuf).setParent(null);
			return val;
		}
	}
}

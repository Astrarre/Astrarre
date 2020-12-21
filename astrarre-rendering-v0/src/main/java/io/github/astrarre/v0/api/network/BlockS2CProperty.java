package io.github.astrarre.v0.api.network;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.base.Env;
import io.github.astrarre.v0.network.PacketByteBuf;
import io.github.astrarre.v0.util.Id;
import io.github.astrarre.v0.util.math.BlockPos;
import io.github.astrarre.v0.world.World;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

public class BlockS2CProperty<T> extends SharedProperty<T> {
	private final Identifier identifier;
	@Nullable private final ServerWorld world;
	private final net.minecraft.util.math.BlockPos pos;

	public static BlockS2CProperty<String> createStringProperty(Id id, World world, BlockPos pos, Consumer<String> onSet) {
		return new BlockS2CProperty<>(PacketByteBuf::writeString, PacketByteBuf::readString, onSet, world, pos, id);
	}

	public static BlockS2CProperty<Integer> createIntProperty(Id id, World world, BlockPos pos, Consumer<Integer> onSet) {
		return new BlockS2CProperty<>(PacketByteBuf::writeInt, PacketByteBuf::readInt, onSet, world, pos, id);
	}

	@SuppressWarnings ("MethodCallSideOnly")
	public BlockS2CProperty(BiConsumer<PacketByteBuf, T> serializer,
			Function<PacketByteBuf, T> deserializer,
			Consumer<T> onSet, World world, BlockPos pos, Id id) {
		super(serializer, deserializer, onSet);

		this.identifier = (Identifier) id;
		this.pos = (net.minecraft.util.math.BlockPos) pos;
		if (world instanceof ServerWorld) {
			this.world = (ServerWorld) world;
		} else {
			this.world = null;
		}
		if (Env.IS_CLIENT) {
			this.registerClient();
		}
	}

	@Environment (EnvType.CLIENT)
	private void registerClient() {
		if (Env.IS_CLIENT) {
			ClientPlayNetworking.registerReceiver(this.identifier,
					(client, handler, buf, sender) -> this.onSet.accept(this.deserializer.apply((PacketByteBuf) buf)));
		}
	}

	@Override
	protected void send(PacketByteBuf buf) {
		if (this.world == null) {
			return;
		}

		CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(this.identifier, (net.minecraft.network.PacketByteBuf) buf);
		for (ServerPlayerEntity entity : PlayerLookup.tracking(this.world, this.pos)) {
			entity.networkHandler.sendPacket(packet);
		}
	}
}

package io.github.astrarre.networking.v0.api;

import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import io.github.astrarre.networking.internal.ByteBufDataInput;
import io.github.astrarre.networking.internal.ByteBufDataOutput;
import io.github.astrarre.networking.mixin.CustomPayloadC2SPacketAccess;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.util.v0.api.Id;
import io.netty.buffer.Unpooled;
import sun.nio.ch.Net;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * a registry for dealing with custom packets
 */
public class ModPacketHandler {
	public static final ModPacketHandler INSTANCE = new ModPacketHandler();

	private final Multimap<Id, ClientReceiver> asyncClientRegistry = HashMultimap.create(), syncClientRegistry = HashMultimap.create();
	private final Multimap<Id, ServerReceiver> asyncServerRegistry = HashMultimap.create(), syncServerRegistry = HashMultimap.create();

	@Environment(EnvType.CLIENT)
	public void sendToServer(Id id, Consumer<Output> output) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		output.accept(new ByteBufDataOutput(buf));
		Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler(), "No Active Server!").sendPacket(new CustomPayloadC2SPacket((Identifier) id, buf));
	}

	@Hide
	public void sendToClient(ServerPlayerEntity entity, Id id, Consumer<Output> out) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		out.accept(new ByteBufDataOutput(buf));
		entity.networkHandler.sendPacket(new CustomPayloadS2CPacket((Identifier) id, buf));
	}

	/**
	 * called when a custom packet is received, there is no guarantee as to what thread this will be invoked from.
	 * At the time of this writing, this method will be called on the network thread
	 * @param id the id of the packet to listen on (commonly known as the 'channel')
	 */
	public void registerClient(Id id, ClientReceiver receiver) {
		this.asyncClientRegistry.put(id, receiver);
	}

	/**
	 * called when a custom packet is received, there is no guarantee as to what thread this will be invoked from.
	 * At the time of this writing, this method will be called on the network thread
	 * @param id the id of the packet to listen on (commonly known as the 'channel')
	 */
	public void registerServer(Id id, ServerReceiver receiver) {
		this.asyncServerRegistry.put(id, receiver);
	}

	/**
	 * called when a custom packet is received on the client, but it is called on the main thread
	 * @param id the id of the packet (commonly known as the 'channel')
	 */
	public void registerSynchronizedClient(Id id, ClientReceiver receiver) {
		this.syncClientRegistry.put(id, receiver);
	}

	/**
	 * called when a custom packet is received on the server, but it is called on the main thread
	 * @param id the id of the packet (commonly known as the 'channel')
	 */
	public void registerSynchronizedServer(Id id, ServerReceiver receiver) {
		this.syncServerRegistry.put(id, receiver);
	}

	/**
	 * @deprecated internal
	 */
	@Hide
	@Deprecated
	@Environment(EnvType.CLIENT)
	public void onReceiveAsync(CustomPayloadS2CPacket packet) {
		this.fire((Id) packet.getChannel(), packet.getData(), this.asyncClientRegistry.get((Id) packet.getChannel()));
	}

	/**
	 * @deprecated internal
	 */
	@Hide
	@Deprecated
	@Environment(EnvType.CLIENT)
	public void onReceive(CustomPayloadS2CPacket packet) {
		this.fire((Id) packet.getChannel(), packet.getData(), this.syncClientRegistry.get((Id) packet.getChannel()));
	}

	/**
	 * @deprecated internal
	 */
	@Hide
	@Deprecated
	public void onReceiveAsync(ServerPlayerEntity entity, CustomPayloadC2SPacket packet) {
		CustomPayloadC2SPacketAccess access = (CustomPayloadC2SPacketAccess) packet;
		Id identifier = (Id) access.getChannel();
		PacketByteBuf data = access.getData();
		this.fire(identifier, data, Iterables.<ServerReceiver, ClientReceiver>transform(this.asyncServerRegistry.get(identifier), c -> (i, d) -> c.accept((NetworkMember)entity, i, d)));
	}

	/**
	 * @deprecated internal
	 */
	@Hide
	@Deprecated
	public void onReceive(ServerPlayerEntity entity, CustomPayloadC2SPacket packet) {
		CustomPayloadC2SPacketAccess access = (CustomPayloadC2SPacketAccess) packet;
		Id identifier = (Id) access.getChannel();
		PacketByteBuf data = access.getData();
		this.fire(identifier, data, Iterables.<ServerReceiver, ClientReceiver>transform(this.syncServerRegistry.get(identifier), c -> (i, d) -> c.accept((NetworkMember)entity, i, d)));
	}

	private void fire(Id identifier, PacketByteBuf data, Iterable<ClientReceiver> listeners) {
		int readerIndex = data.readerIndex();
		ByteBufDataInput input = new ByteBufDataInput(data);
		for (ClientReceiver receiver : listeners) {
			data.readerIndex(readerIndex);
			receiver.accept(identifier, input);
			input.reset();
		}
	}

	public interface ClientReceiver {
		void accept(Id id, Input buf);
	}

	public interface ServerReceiver {
		void accept(NetworkMember member, Id id, Input buf);
	}
}

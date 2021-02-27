package io.github.astrarre.networking.v0.api;

import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.astrarre.networking.internal.ByteBufDataInput;
import io.github.astrarre.networking.mixin.CustomPayloadC2SPacketAccess;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.fabric.FabricData;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.util.v0.api.Id;
import io.netty.buffer.Unpooled;

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

	private final Multimap<Id, Receiver> asyncClientRegistry = HashMultimap.create(),
			asyncServerRegistry = HashMultimap.create(),
			syncClientRegistry = HashMultimap.create(),
			syncServerRegistry = HashMultimap.create();

	@Environment(EnvType.CLIENT)
	public void sendToServer(Id id, Consumer<Output> output) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		output.accept((Output) buf);
		Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler(), "No Active Server!").sendPacket(new CustomPayloadC2SPacket((Identifier) id, buf));
	}

	// todo make ServerPlayerEntity abstraction in Astrarre.main have extension method for sending packet
	@Hide
	public void sendToClient(ServerPlayerEntity entity, Id id, Consumer<Output> out) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		out.accept((Output) buf);
		entity.networkHandler.sendPacket(new CustomPayloadS2CPacket((Identifier) id, buf));
	}

	/**
	 * called when a custom packet is received, there is no guarantee as to what thread this will be invoked from.
	 * At the time of this writing, this method will be called on the network thread
	 * @param id the id of the packet to listen on (commonly known as the 'channel')
	 */
	public void registerClient(Id id, Receiver receiver) {
		this.asyncClientRegistry.put(id, receiver);
	}

	/**
	 * called when a custom packet is received, there is no guarantee as to what thread this will be invoked from.
	 * At the time of this writing, this method will be called on the network thread
	 * @param id the id of the packet to listen on (commonly known as the 'channel')
	 */
	public void registerServer(Id id, Receiver receiver) {
		this.asyncServerRegistry.put(id, receiver);
	}

	/**
	 * called when a custom packet is received on the client, but it is called on the main thread
	 * @param id the id of the packet (commonly known as the 'channel')
	 */
	public void registerSynchronizedClient(Id id, Receiver receiver) {
		this.syncClientRegistry.put(id, receiver);
	}

	/**
	 * called when a custom packet is received on the server, but it is called on the main thread
	 * @param id the id of the packet (commonly known as the 'channel')
	 */
	public void registerSynchronizedServer(Id id, Receiver receiver) {
		this.syncServerRegistry.put(id, receiver);
	}

	/**
	 * @deprecated internal
	 */
	@Hide
	@Deprecated
	@Environment(EnvType.CLIENT)
	public void onReceiveAsync(CustomPayloadS2CPacket packet) {
		this.fire((Id) packet.getChannel(), packet.getData(), this.asyncClientRegistry);
	}

	/**
	 * @deprecated internal
	 */
	@Hide
	@Deprecated
	@Environment(EnvType.CLIENT)
	public void onReceive(CustomPayloadS2CPacket packet) {
		this.fire((Id) packet.getChannel(), packet.getData(), this.syncClientRegistry);
	}

	/**
	 * @deprecated internal
	 */
	@Hide
	@Deprecated
	public void onReceiveAsync(CustomPayloadC2SPacket packet) {
		CustomPayloadC2SPacketAccess access = (CustomPayloadC2SPacketAccess) packet;
		Id identifier = (Id) access.getChannel();
		PacketByteBuf data = access.getData();
		this.fire(identifier, data, this.asyncServerRegistry);
	}

	/**
	 * @deprecated internal
	 */
	@Hide
	@Deprecated
	public void onReceive(CustomPayloadC2SPacket packet) {
		CustomPayloadC2SPacketAccess access = (CustomPayloadC2SPacketAccess) packet;
		Id identifier = (Id) access.getChannel();
		PacketByteBuf data = access.getData();
		this.fire(identifier, data, this.syncServerRegistry);
	}

	private void fire(Id identifier, PacketByteBuf data, Multimap<Id, Receiver> listeners) {
		int readerIndex = data.readerIndex();
		ByteBufDataInput input = new ByteBufDataInput(data);
		for (Receiver receiver : listeners.get(identifier)) {
			data.readerIndex(readerIndex);
			receiver.accept(identifier, input);
			input.reset();
		}
	}

	public interface Receiver {
		void accept(Id id, Input buf);
	}
}

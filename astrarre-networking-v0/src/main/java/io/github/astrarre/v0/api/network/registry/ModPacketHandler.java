package io.github.astrarre.v0.api.network.registry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.astrarre.internal.networking.mixin.CustomPayloadC2SPacketAccess;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.v0.network.PacketByteBuf;
import io.github.astrarre.v0.util.Id;

import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;

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

	@Hide
	@Environment(EnvType.CLIENT)
	public void onReceiveAsync(CustomPayloadS2CPacket packet) {
		this.fire((Id) packet.getChannel(), (PacketByteBuf) packet.getData(), this.asyncClientRegistry);
	}

	@Hide
	@Environment(EnvType.CLIENT)
	public void onReceive(CustomPayloadS2CPacket packet) {
		this.fire((Id) packet.getChannel(), (PacketByteBuf) packet.getData(), this.syncClientRegistry);
	}

	@Hide
	public void onReceiveAsync(CustomPayloadC2SPacket packet) {
		CustomPayloadC2SPacketAccess access = (CustomPayloadC2SPacketAccess) packet;
		Id identifier = (Id) access.getChannel();
		PacketByteBuf data = (PacketByteBuf) access.getData();
		this.fire(identifier, data, this.asyncServerRegistry);
	}

	@Hide
	public void onReceive(CustomPayloadC2SPacket packet) {
		CustomPayloadC2SPacketAccess access = (CustomPayloadC2SPacketAccess) packet;
		Id identifier = (Id) access.getChannel();
		PacketByteBuf data = (PacketByteBuf) access.getData();
		this.fire(identifier, data, this.syncServerRegistry);
	}

	private void fire(Id identifier, PacketByteBuf data, Multimap<Id, Receiver> listeners) {
		for (Receiver receiver : listeners.get(identifier)) {
			receiver.accept(identifier, data);
		}
	}

	public interface Receiver {

		void accept(Id id, PacketByteBuf buf);
	}
}

package io.github.astrarre.gui.internal.comms;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.astrarre.gui.internal.access.ServerPlayerEntityAccess;
import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.comms.PacketKey;
import io.github.astrarre.hash.v0.api.HashKey;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.astrarre.util.v0.api.Id;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class AbstractComms implements PacketHandler {
	public static final Map<HashKey, Client> CLIENT_PAIRS = new HashMap<>();
	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	public static final Identifier PACKET_ID = new Identifier("astrarre", "gui_comms");

	final Multimap<HashKey, Consumer<NBTagView>> tag = HashMultimap.create();
	final Map<HashKey, NBTagView> queue = new HashMap<>();
	final HashKey uniqueId;
	PacketByteBuf packetQueue;
	int queueCounter;

	public PacketByteBuf getPacketQueue() {
		PacketByteBuf buf = this.packetQueue;
		if(buf == null) {
			this.packetQueue = buf = new PacketByteBuf(Unpooled.buffer());
		}
		return buf;
	}

	protected AbstractComms(HashKey id) {
		this.uniqueId = id;
	}

	@Override
	public void listen(PacketKey key, Consumer<NBTagView> consumer) {
		HashKey hash = key.hash();
		this.tag.put(hash, consumer);

		NBTagView view = this.queue.remove(hash);
		if(view != null) {
			for(Consumer<NBTagView> cons : this.tag.get(hash)) {
				cons.accept(view);
			}
		}
	}

	public static AbstractComms getOrOpenPlayerComms(PlayerEntity entity, HashKey uniqueId, boolean isClient) {
		if(isClient) {
			return CLIENT_PAIRS.computeIfAbsent(uniqueId, i -> new Client(CLIENT.getNetworkHandler(), i));
		} else {
			var v = ((ServerPlayerEntityAccess)entity).astrarre_coms();
			return v.computeIfAbsent(uniqueId, key -> new Server((ServerPlayerEntity) entity, uniqueId));
		}
	}

	protected abstract void send(PacketByteBuf buf);

	@Override
	public void startQueue() {
		if(this.queueCounter <= 0 && this.packetQueue != null) {
			this.send(this.packetQueue);
			this.packetQueue = null;
		}
		this.queueCounter++;
	}

	@Override
	public void flushQueue() {
		if(--this.queueCounter <= 0 && this.packetQueue != null) {
			this.send(this.packetQueue);
			this.packetQueue = null;
			this.queueCounter = 0;
		}
	}

	@Override
	public void sendInfo(PacketKey key, NBTagView packet) { // todo add batched packets
		PacketByteBuf buf = this.queueCounter > 0 ? this.getPacketQueue() : new PacketByteBuf(Unpooled.buffer());
		NbtCompound compound = new NbtCompound();
		compound.put("packet", packet.toTag());

		if((this.packetQueue != null && this.packetQueue.writerIndex() == 0) || this.queueCounter <= 0) {
			this.uniqueId.write(buf);
		}
		key.hash().write(buf);
		buf.writeNbt(compound);
		if(this.queueCounter <= 0) {
			this.send(buf);
		}
	}

	public void onReceive(PacketByteBuf buf) {
		HashKey key = new HashKey(buf);
		NBTagView view = FabricViews.view(buf.readNbt());
		NBTagView packet = view.getTag("packet");
		var cons = this.tag.get(key);
		if(cons == null || cons.isEmpty()) {
			this.queue.put(key, packet);
		} else {
			for(Consumer<NBTagView> con : cons) {
				con.accept(packet);
			}
		}
	}

	public static class Server extends AbstractComms {
		public final ServerPlayerEntity player;

		public Server(ServerPlayerEntity player, HashKey uniqueId) {
			super(uniqueId);
			this.player = player;
		}

		@Override
		public void close() {
			((ServerPlayerEntityAccess)this.player).astrarre_coms().remove(this.uniqueId);
		}

		@Override
		protected void send(PacketByteBuf buf) {
			CustomPayloadS2CPacket pkt = new CustomPayloadS2CPacket(PACKET_ID, buf);
			this.player.networkHandler.sendPacket(pkt);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Client extends AbstractComms {
		final ClientPlayNetworkHandler networkHandler;

		public Client(ClientPlayNetworkHandler handler, HashKey uniqueId) {
			super(uniqueId);
			this.networkHandler = handler;
		}

		@Override
		public void close() {
			CLIENT_PAIRS.remove(this.uniqueId);
		}

		@Override
		protected void send(PacketByteBuf buf) {
			CustomPayloadC2SPacket pkt = new CustomPayloadC2SPacket(PACKET_ID, buf);
			this.networkHandler.sendPacket(pkt);
		}
	}
}

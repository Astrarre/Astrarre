package io.github.astrarre.gui.internal.comms;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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

	final Map<HashKey, Consumer<NBTagView>> tag = new HashMap<>();
	final Map<HashKey, NBTagView> queue = new HashMap<>();
	final HashKey uniqueId;

	protected AbstractComms(HashKey id) {
		this.uniqueId = id;
	}

	@Override
	public void listen(PacketKey key, Consumer<NBTagView> consumer) {
		HashKey hash = key.hash();
		this.tag.put(hash, consumer);
		NBTagView view = this.queue.remove(hash);
		if(view != null) {
			consumer.accept(view);
		}
	}

	public static PacketHandler getOrOpenPlayerComms(PlayerEntity entity, HashKey uniqueId, boolean isClient) {
		if(isClient) {
			return CLIENT_PAIRS.computeIfAbsent(uniqueId, i -> new Client(CLIENT.getNetworkHandler(), i));
		} else {
			var v = ((ServerPlayerEntityAccess)entity).astrarre_coms();
			Server server = new Server((ServerPlayerEntity) entity, uniqueId);
			v.put(uniqueId, server);
			return server;
		}
	}

	protected abstract void send(PacketByteBuf buf);

	@Override
	public void sendInfo(PacketKey key, Consumer<NBTagView.Builder> packet) { // todo add batched packets
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		NbtCompound compound = new NbtCompound();

		compound.put("key", key.hash().write().asMinecraft());

		NBTagView.Builder builder = NBTagView.builder();
		packet.accept(builder);
		compound.put("packet", builder.toTag());

		this.uniqueId.write(buf);
		buf.writeNbt(compound);
		this.send(buf);
	}

	public void onReceive(PacketByteBuf buf) {
		NBTagView view = FabricViews.view(buf.readNbt());
		NBTagView packet = view.getTag("packet");

		HashKey key = new HashKey(view.getValue("key"));
		var cons = this.tag.get(key);
		if(cons == null) {
			this.queue.put(key, packet);
		} else {
			cons.accept(packet);
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

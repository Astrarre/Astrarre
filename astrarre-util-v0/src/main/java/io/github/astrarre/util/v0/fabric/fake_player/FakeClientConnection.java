package io.github.astrarre.util.v0.fabric.fake_player;

import java.net.SocketAddress;

import javax.crypto.Cipher;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.text.Text;

public class FakeClientConnection extends ClientConnection {
	private static final SocketAddress DUMMY = new SocketAddress() {
		@Override
		public String toString() {
			return "fake_player";
		}
	};
	private final PacketListener dummyListener = new PacketListener() {
		@Override
		public void onDisconnected(Text reason) {

		}

		@Override
		public ClientConnection getConnection() {
			return FakeClientConnection.this;
		}
	};

	public static final FakeClientConnection INSTANCE = new FakeClientConnection(NetworkSide.CLIENTBOUND);

	public FakeClientConnection(NetworkSide side) {
		super(side);
	}

	@Override
	public void send(Packet<?> packet) {
	}

	@Override
	public void send(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
	}

	@Override
	public boolean acceptInboundMessage(Object msg) throws Exception {
		return true;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	protected void ensureNotSharable() {
	}

	@Override
	public boolean isSharable() {
		return true;
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
	}

	@Override
	public void setState(NetworkState state) {
	}

	@Override
	public void channelInactive(ChannelHandlerContext channelHandlerContext) {
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet) {
	}

	@Override
	public void setPacketListener(PacketListener listener) {
	}

	@Override
	public void tick() {
	}

	@Override
	protected void updateStats() {
	}

	@Override
	public SocketAddress getAddress() {
		return DUMMY;
	}

	@Override
	public void disconnect(Text disconnectReason) {
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public void setupEncryption(Cipher cipher, Cipher cipher2) {
	}

	@Override
	public boolean isEncrypted() {
		return true;
	}

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public boolean hasChannel() {
		return true;
	}

	@Override
	public PacketListener getPacketListener() {
		return this.dummyListener;
	}

	@Nullable
	@Override
	public Text getDisconnectReason() {
		return null;
	}

	@Override
	public void disableAutoRead() {
	}

	@Override
	public void setCompressionThreshold(int compressionThreshold, boolean rejectsBadPackets) {
	}

	@Override
	public void handleDisconnection() {
	}

	@Override
	public float getAveragePacketsReceived() {
		return 1;
	}

	@Override
	public float getAveragePacketsSent() {
		return 1;
	}
}

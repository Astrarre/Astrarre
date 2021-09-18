package io.github.astrarre.gui.internal.comms;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.comms.PacketKey;
import io.github.astrarre.hash.v0.api.HashKey;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;

public final class LocalComms implements PacketHandler {
	record Comms(LocalComms client, LocalComms server) {}
	static final Map<HashKey, Comms> LOCAL_COMMS_MAP = new HashMap<>();

	public static LocalComms getOrCreate(HashKey key, boolean client) {
		Comms comms = LOCAL_COMMS_MAP.computeIfAbsent(key, k -> {
			LocalComms com = new LocalComms(k);
			return new Comms(com, com.other);
		});
		return client ? comms.client() : comms.server();
	}

	final LocalComms other;
	final Multimap<HashKey, Consumer<NBTagView>> tag = HashMultimap.create();
	final Map<HashKey, NBTagView> queue = new HashMap<>();
	final HashKey uniqueId;

	private LocalComms(LocalComms other, HashKey id) {
		this.other = other;
		this.uniqueId = id;
	}

	public LocalComms(HashKey id) {
		this.other = new LocalComms(this, id);
		this.uniqueId = id;
	}

	@Override
	public void sendInfo(PacketKey key, NBTagView view) {

	}

	@Override
	public void listen(PacketKey key, Consumer<NBTagView> consumer) {

	}

	@Override
	public void close() {
		LOCAL_COMMS_MAP.remove(this.uniqueId);
	}

	@Override
	public void startQueue() {
	}

	@Override
	public void flushQueue() {

	}
}

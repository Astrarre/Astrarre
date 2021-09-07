package io.github.astrarre.gui.v1.api.component.slot;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.github.astrarre.gui.internal.mixin.ScreenHandlerAccess;
import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.server.ServerPanel;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;

public class ASlotHelper {
	public static void linkAllFromServer(PacketHandler handler, ServerPanel panel, List<SlotKey> keys) {
		for(SlotKey key : keys) {
			linkFromServer(handler, panel, key);
		}
	}

	/**
	 * This method must be called from the server init in {@link ServerPanel#openHandled(PlayerEntity, ServerPanel.ClientInit,
	 * ServerPanel.ServerInit)}. It links the slots on the server with the ones on the client, allowing the itemstacks in the inventory to be
	 * synchronized.
	 */
	public static void linkFromServer(PacketHandler packet, ServerPanel panel, SlotKey key) {
		ScreenHandler handler = panel.screenHandler();
		packet.sendInfo(key, builder -> {
			var mc = new ASlot.Minecraft(key.inventory, key.slotIndex, key);
			((ScreenHandlerAccess) handler).callAddSlot(mc);
			builder.putInt("index", mc.id);
		});
	}

	public static List<SlotKey> player(PlayerEntity entity, int inventoryId) {
		return player(entity.getInventory(), inventoryId);
	}

	/**
	 * @return a list of slot keys, for a player's inventory ordered 0-36. Links hotbar and main inventory together for shift-click transfer.
	 */
	public static List<SlotKey> player(PlayerInventory inventory, int inventoryId) {
		List<SlotKey> hotbar = inv(inventory, 0, 9, inventoryId), main = inv(inventory, 9, 36, inventoryId);
		hotbar.forEach(key -> key.linkAll(main));
		main.forEach(key -> key.linkAll(hotbar));

		List<SlotKey> combined = new ArrayList<>(hotbar.size() + main.size());
		combined.addAll(hotbar);
		combined.addAll(main);
		return combined;
	}

	public static List<SlotKey> inv(Inventory inventory, int inventoryId) {
		return inv(inventory, 0, inventory.size(), inventoryId);
	}

	public static List<SlotKey> inv(Inventory inventory, int from, int len, int inventoryId) {
		List<SlotKey> keys = new ArrayList<>(inventory.size());
		for(int i = 0; i < len; i++) {
			keys.add(new SlotKey(inventory, inventoryId, i + from));
		}
		return keys;
	}
}

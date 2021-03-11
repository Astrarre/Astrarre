package io.github.astrarre.gui.v0.fabric.adapter.slot;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class PlayerSlot extends Slot {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "player_slot"), PlayerSlot::new);

	@Environment(EnvType.CLIENT)
	private PlayerSlot(Input input) {
		super(ENTRY, input);
	}

	public PlayerSlot(PlayerInventory inventory, int index) {
		super(ENTRY, inventory, index);
	}

	public PlayerSlot(NetworkMember member, int index) {
		super(ENTRY, member.to().inventory, index);
	}

	public static void init() {
	}

	@Override
	protected void writeInventoryData(Output output, Inventory inventory) {}

	@Override
	protected Inventory readInventoryData(Input input) {
		PlayerEntity player = MinecraftClient.getInstance().player;
		return player.inventory;
	}
}

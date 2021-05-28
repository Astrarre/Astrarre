package io.github.astrarre.gui.v0.fabric.adapter.slot;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class APlayerSlot extends ASlot {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "player_slot"), APlayerSlot::new);
	@Environment(EnvType.CLIENT)
	private APlayerSlot(NBTagView input) {
		super(ENTRY, input);
	}

	public APlayerSlot(PlayerInventory inventory, int index) {
		super(ENTRY, inventory, index);
	}

	public APlayerSlot(NetworkMember member, int index) {
		super(ENTRY, member.to().getInventory(), index);
	}

	public static void init() {
	}

	@Override
	protected void writeInventoryData(NBTagView.Builder output, Inventory inventory) {}

	@Override
	protected Inventory readInventoryData(NBTagView input) {
		PlayerEntity player = MinecraftClient.getInstance().player;
		return player.getInventory();
	}

	@Override
	public String toString() {
		return "APlayerSlot " + ((PlayerInventory)this.inventory).player.getEntityName();
	}
}

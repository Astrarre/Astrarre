package io.github.astrarre.internal.gui;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;

public class GuiScreenHandler extends ScreenHandler {
	// todo fix
	public static final ScreenHandlerType<GuiScreenHandler> TYPE = Registry.register(Registry.SCREEN_HANDLER, new Identifier("testmod", "test"), new ExtendedScreenHandlerType<>(
			GuiScreenHandler::new));

	// todo convert PacketByteBuf to Gui or something
	public GuiScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
		this(syncId, playerInventory);
	}

	public GuiScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(TYPE, syncId);

		//The player inventory
		for (int m = 0; m < 3; ++m) {
			for (int l = 0; l < 9; ++l) {
				this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
			}
		}
		//The player Hotbar
		for (int m = 0; m < 9; ++m) {
			this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}
}

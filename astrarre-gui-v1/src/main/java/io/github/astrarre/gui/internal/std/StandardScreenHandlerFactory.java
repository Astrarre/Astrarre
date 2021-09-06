package io.github.astrarre.gui.internal.std;

import io.github.astrarre.gui.internal.GuiInternal;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class StandardScreenHandlerFactory implements NamedScreenHandlerFactory {
	public static final StandardScreenHandlerFactory INSTANCE = new StandardScreenHandlerFactory();
	@Override
	public Text getDisplayName() {
		return GuiInternal.TEXT;
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return GuiInternal.HANDLER_TYPE.create(syncId, inv);
	}
}

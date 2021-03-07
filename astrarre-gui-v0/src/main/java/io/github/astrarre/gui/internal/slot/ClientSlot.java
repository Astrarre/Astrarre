package io.github.astrarre.gui.internal.slot;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.fabric.adapter.Slot;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.item.ItemStack;

public final class ClientSlot extends Slot {
	private static final DrawableRegistry.Entry CLIENT = DrawableRegistry.register(Id.create("astrarre-gui-v0", "client_slot"),
			ClientSlot::new);

	private ItemStack stack = ItemStack.EMPTY;

	protected ClientSlot(RootContainer rootContainer) {
		super(rootContainer, CLIENT);
	}

	public ClientSlot(RootContainer rootContainer, Input input) {
		super(rootContainer, CLIENT, input);
	}

	@Override
	public ItemStack getStack() {
		return this.stack;
	}

	@Override
	public void setStack(ItemStack stack) {
		this.stack = stack;
	}
}
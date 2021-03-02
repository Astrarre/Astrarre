package io.github.astrarre.gui.v0.api.drawable;

import io.github.astrarre.gui.internal.access.ExtraSlotAccess;
import io.github.astrarre.gui.internal.containers.HandledScreenRootContainer;
import io.github.astrarre.gui.internal.containers.slot.SlotInventory;
import io.github.astrarre.gui.internal.mixin.HandledScreenAccessor;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.fabric.FabricData;
import io.github.astrarre.rendering.internal.MatrixGraphics;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.stripper.Hide;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.item.ItemStack;

public class Slot extends Drawable implements Interactable {
	private final SlotInventory inventory;

	@Hide
	protected net.minecraft.screen.slot.Slot minecraftSlot;
	private boolean isHover;

	protected Slot(RootContainer rootContainer, DrawableRegistry.Entry id) {
		super(rootContainer, id);
		this.validate(rootContainer);
		this.inventory = new SlotInventory();
		this.minecraftSlot = new MinecraftSlot();
		((HandledScreenRootContainer)rootContainer).addSlot(this.minecraftSlot);
	}

	protected Slot(RootContainer rootContainer, DrawableRegistry.Entry id, Input input) {
		this(rootContainer, id);
		this.inventory.stack = FabricData.readStack(input);
	}

	public Slot(RootContainer rootContainer) {
		this(rootContainer, DrawableRegistry.SLOT);
		if(this.getClass() != Slot.class) {
			throw new IllegalStateException("If Slot is extended, you must use the constructors that take DrawableRegistry$Entry as a parameter!");
		}
	}

	public Slot(RootContainer rootContainer, Input input) {
		this(rootContainer);
		this.inventory.stack = FabricData.readStack(input);
	}

	@Override
	protected void write0(Output output) {
		FabricData.from(output).writeItemStack(this.inventory.stack);
	}

	protected final void validate(RootContainer rootContainer) {
		Validate.isTrue(rootContainer instanceof HandledScreenRootContainer, "cannot add slot to non-handled screens!");
	}

	@Override
	protected void render0(Graphics3d graphics, float tickDelta) {
		Validate.isTrue(graphics instanceof MatrixGraphics, "Slot can only be rendered with matrix graphics!");
		((HandledScreenAccessor) ((HandledScreenRootContainer) this.rootContainer).screen).callDrawSlot(((MatrixGraphics) graphics).matrices, this.minecraftSlot);
		if(this.isHover) {
			graphics.fillGradient(16, 16, 0x80ffffff, 0x80ffffff);
		}
	}

	@Hide
	protected ItemStack getStack() {
		return this.inventory.stack;
	}

	@Hide
	protected void setStack(ItemStack stack) {
		this.inventory.stack = stack;
	}

	@Override
	public boolean mouseHover(double mouseX, double mouseY) {
		this.isHover = true;
		return true;
	}

	private class MinecraftSlot extends net.minecraft.screen.slot.Slot implements ExtraSlotAccess {
		public MinecraftSlot() {
			super(Slot.this.inventory, 0, 0, 0);
		}

		@Override
		public boolean astrarre_isPointOverSlot(double x, double y) {
			return Slot.this.rootContainer.getContentPanel().drawableAt(x, y) == Slot.this;
		}
	}
}

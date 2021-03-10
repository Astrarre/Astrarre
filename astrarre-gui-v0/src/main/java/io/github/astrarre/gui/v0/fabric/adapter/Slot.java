package io.github.astrarre.gui.v0.fabric.adapter;

import io.github.astrarre.gui.internal.access.ExtraSlotAccess;
import io.github.astrarre.gui.internal.access.SlotAddAccess;
import io.github.astrarre.gui.internal.slot.ClientSlot;
import io.github.astrarre.gui.internal.slot.SlotInventory;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.fabric.FabricData;
import io.github.astrarre.rendering.internal.MatrixGraphics;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.graphics.DelegateGraphics;
import io.github.astrarre.rendering.v0.api.textures.Texture;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class Slot extends Drawable implements Interactable {
	public static final Polygon SQUARE_16x16 = new Polygon.Builder(4).addVertex(0, 0).addVertex(0, 18).addVertex(18, 18).addVertex(18, 0).build();
	private static final Texture INVENTORY_TEXTURE = new Texture("minecraft", "textures/gui/container/furnace.png", 256, 256);
	private static final DrawableRegistry.Entry INVENTORY = DrawableRegistry
			                                                        .register(Id.create("astrarre-gui-v0", "inventory_slot"), ClientSlot::new);
	private static final Transformation TRANSFORMATION = Transformation.translate(1, 1, 0);
	private final SlotInventory inventory;
	protected MinecraftSlot minecraftSlot;
	private boolean highlighted;
	private boolean tooltip;
	private double mX, mY;

	@Environment (EnvType.CLIENT) private boolean render;
	@Environment (EnvType.CLIENT) private ItemStack override;

	protected Slot(RootContainer rootContainer, DrawableRegistry.Entry id, Input input) {
		super(rootContainer, id);
		this.setStack(FabricData.readStack(input));
		this.validate(rootContainer);
		this.setBounds(SQUARE_16x16);
		this.inventory = new SlotInventory(this);
		this.minecraftSlot = new MinecraftSlot();
		this.minecraftSlot.override = input.readInt();
		((SlotAddAccess) rootContainer).addSlot(this.minecraftSlot);
	}

	protected final void validate(RootContainer rootContainer) {
		Validate.isTrue(rootContainer instanceof SlotAddAccess, "cannot add slot to non-handled screens!");
	}

	protected Slot(RootContainer rootContainer, DrawableRegistry.Entry id) {
		super(rootContainer, id);
		this.validate(rootContainer);
		this.setBounds(SQUARE_16x16);
		this.inventory = new SlotInventory(this);
		this.minecraftSlot = new MinecraftSlot();
		((SlotAddAccess) rootContainer).addSlot(this.minecraftSlot);
	}

	public static Slot inventorySlot(RootContainer container, Inventory inventory, int index) {
		return new Slot(container, INVENTORY) {
			@Override
			public ItemStack getStack() {
				return inventory.getStack(index);
			}

			@Override
			public void setStack(ItemStack stack) {
				inventory.setStack(index, stack);
			}

			@Override
			public boolean isValid(ItemStack stack) {
				return inventory.isValid(index, stack);
			}
		};
	}

	@Override
	protected void render0(Graphics3d graphics, float tickDelta) {
		Validate.isTrue(DelegateGraphics.resolve(graphics) instanceof MatrixGraphics, "Slot can only be rendered with matrix graphics!");
		this.renderBackground(graphics, tickDelta);
		if (this.render) {
			graphics.drawItem(this.override == null ? this.minecraftSlot.getStack() : this.override);
			if (this.highlighted) {
				this.renderGradient(graphics, tickDelta);
			}
		}
	}

	protected void renderBackground(Graphics3d graphics, float tickDelta) {
		graphics.drawTexture(INVENTORY_TEXTURE, 55, 16, 18, 18);
	}

	protected void renderGradient(Graphics3d graphics, float tickDelta) {
		try (Close close = graphics.applyTransformation(TRANSFORMATION)) {
			graphics.fillGradient(16, 16, 0x80ffffff, 0x80ffffff);
		}
	}

	@Override
	protected void write0(Output output) {
		FabricData.from(output).writeItemStack(this.getStack());
		output.writeInt(this.minecraftSlot.id);
	}

	public abstract ItemStack getStack();

	public abstract void setStack(ItemStack stack);

	@Override
	public boolean isHovering(double mouseX, double mouseY) {
		return true;
	}

	public boolean isValid(ItemStack stack) {
		return true;
	}

	private class MinecraftSlot extends net.minecraft.screen.slot.Slot implements ExtraSlotAccess {
		public int override = -1;

		public MinecraftSlot() {
			super(Slot.this.inventory, 0, 0, 0);
		}

		@Override
		public boolean astrarre_isPointOverSlot(double x, double y) {
			return Slot.this.rootContainer.getContentPanel().drawableAt(x, y) == Slot.this;
		}

		@Override
		public void setHighlighted(boolean highlighted) {
			Slot.this.highlighted = highlighted;
		}

		@Override
		public void setRender(boolean render) {
			Slot.this.render = render;
		}

		@Override
		public void setOverride(ItemStack stack) {
			Slot.this.override = stack;
		}

		@Override
		public int idOverride() {
			return this.override;
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return this.inventory.isValid(0, stack);
		}
	}
}

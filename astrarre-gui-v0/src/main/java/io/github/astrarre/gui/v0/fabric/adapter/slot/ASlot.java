package io.github.astrarre.gui.v0.fabric.adapter.slot;

import java.util.Map;
import java.util.WeakHashMap;

import io.github.astrarre.gui.internal.access.ExtraSlotAccess;
import io.github.astrarre.gui.internal.access.SlotAddAccess;
import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class ASlot extends ADrawable implements Interactable {
	public static final Polygon SQUARE_16x16 = new Polygon.Builder(4).addVertex(0, 0).addVertex(0, 18).addVertex(18, 18).addVertex(18, 0).build();
	public static final Sprite.Sized SLOT = Sprite.of(Id.create("minecraft", "textures/gui/container/furnace.png"))
			.cutout(55/256f, 16/256f, 18/256f, 18/256f)
			.sized(18, 18);
	private static final Transformation TRANSFORMATION = Transformation.translate(1, 1, 0);
	private final Inventory inventory;
	private final int index;
	protected Map<RootContainer, MinecraftSlot> minecraftSlots = new WeakHashMap<>();
	protected boolean highlighted;

	@Environment(EnvType.CLIENT) private int overrideClient = -1;
	@Environment (EnvType.CLIENT) protected boolean render;
	@Environment (EnvType.CLIENT) private ItemStack override;

	protected ASlot(DrawableRegistry.Entry id, Inventory inventory, int index) {
		super(id);
		this.inventory = inventory;
		this.index = index;
		this.setBounds(SQUARE_16x16);
	}

	@Environment(EnvType.CLIENT)
	protected ASlot(DrawableRegistry.Entry id, NBTagView input) {
		super(id);
		this.inventory = this.readInventoryData(input);
		this.index = input.getInt("index");
		this.overrideClient = input.getInt("overrideClient");
		this.setBounds(SQUARE_16x16);
	}

	/**
	 * write any data required to find the corresponding inventory on the other side (client)
	 */
	protected abstract void writeInventoryData(NBTagView.Builder output, Inventory inventory);
	@Environment(EnvType.CLIENT)
	protected abstract Inventory readInventoryData(NBTagView input);

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		this.writeInventoryData(output, this.inventory);
		output.putInt("index", this.index);
		output.putInt("overrideClient", this.minecraftSlots.get(container).id);
	}

	@Override
	protected void onAdded(RootContainer container) {
		super.onAdded(container);
		Validate.isTrue(container instanceof SlotAddAccess, "cannot add slot to non-handled screens!");
		MinecraftSlot slot = new MinecraftSlot(container);
		this.minecraftSlots.put(container, slot);
		if(this.isClient()) {
			if(this.overrideClient != -1) {
				slot.override = this.overrideClient;
				slot.id = this.overrideClient;
			}
		}
		((SlotAddAccess) container).addSlot(slot);
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		this.renderBackground(graphics, tickDelta);
		if (this.render) {
			graphics.drawItem(this.override == null ? this.getStack() : this.override);
			if (this.highlighted) {
				this.renderGradient(graphics, tickDelta);
			}
		}
	}

	protected void renderBackground(Graphics3d graphics, float tickDelta) {
		graphics.drawSprite(SLOT);
	}

	protected void renderGradient(Graphics3d graphics, float tickDelta) {
		try (Close close = graphics.applyTransformation(TRANSFORMATION)) {
			graphics.fillGradient(16, 16, 0x80ffffff, 0x80ffffff);
		}
	}

	public ItemStack getStack() {
		return this.inventory.getStack(this.index);
	}

	public void setStack(ItemStack stack) {
		this.inventory.setStack(this.index, stack);
	}

	@Override
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		return true;
	}

	private class MinecraftSlot extends net.minecraft.screen.slot.Slot implements ExtraSlotAccess {
		public int override = -1;
		public final RootContainer container;

		public MinecraftSlot(RootContainer container) {
			super(ASlot.this.inventory, ASlot.this.index, 0, 0);
			this.container = container;
		}

		@Override
		public boolean astrarre_isPointOverSlot(double x, double y) {
			Object o = this.container.getContentPanel().drawableAt(this.container, x, y);
			return o == ASlot.this;
		}

		@Override
		public void setHighlighted(boolean highlighted) {
			ASlot.this.highlighted = highlighted;
		}

		@Override
		public void setRender(boolean render) {
			ASlot.this.render = render;
		}

		@Override
		public void setOverride(ItemStack stack) {
			ASlot.this.override = stack;
		}

		@Override
		public int idOverride() {
			return this.override;
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return this.inventory.isValid(ASlot.this.index, stack);
		}
	}
}
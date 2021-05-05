package io.github.astrarre.gui.v0.fabric.adapter.slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import io.github.astrarre.gui.internal.access.ExtraSlotAccess;
import io.github.astrarre.gui.internal.access.SlotAddAccess;
import io.github.astrarre.gui.internal.containers.ScreenHandlerContainer;
import io.github.astrarre.gui.internal.mixin.ScreenHandlerAccess;
import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Slots are 18x18 by default todo gui editor/maker todo split Graphics
 *
 * @see ABlockEntityInventorySlot
 * @see APlayerSlot
 */
public abstract class ASlot extends ADrawable implements Interactable {
	public static final Polygon SQUARE_18x18 = new Polygon.Builder(4).addVertex(0, 0).addVertex(0, 18).addVertex(18, 18).addVertex(18, 0).build();
	public static final Sprite.Sized SLOT = Sprite.of(Id.create("minecraft", "textures/gui/container/furnace.png"))
			                                        .cutout(55 / 256f, 16 / 256f, 18 / 256f, 18 / 256f).sized(18, 18);
	private static final Transformation TRANSFORMATION = Transformation.translate(1, 1, 300);
	private final int index;
	protected Inventory inventory;
	protected Map<RootContainer, MinecraftSlot> minecraftSlots = new WeakHashMap<>();
	protected Map<RootContainer, List<ASlot>> targetSlotIds = new WeakHashMap<>();
	protected IntList temp;
	protected boolean highlighted;
	@Environment (EnvType.CLIENT) protected boolean render;
	@Environment (EnvType.CLIENT) protected IntList slotIds;
	@Environment (EnvType.CLIENT) private int overrideClient = -1;
	@Environment (EnvType.CLIENT) private ItemStack override;

	protected ASlot(DrawableRegistry.Entry id, Inventory inventory, int index) {
		super(id);
		this.inventory = inventory;
		this.index = index;
		this.setBounds(SQUARE_18x18);
	}

	@Environment (EnvType.CLIENT)
	protected ASlot(DrawableRegistry.Entry id, NBTagView input) {
		super(id);
		this.inventory = this.readInventoryData(input);
		this.index = input.getInt("index");
		this.overrideClient = input.getInt("overrideClient");
		if (input.hasKey("targetSlotIds")) {
			this.temp = input.get("targetSlotIds", NBTType.INT_ARRAY);
		}
		this.setBounds(SQUARE_18x18);
	}

	@Environment (EnvType.CLIENT)
	protected abstract Inventory readInventoryData(NBTagView input);

	/**
	 * @see #link(RootContainer, ASlot) but max priority (inserted at begining of list)
	 */
	public void linkAllFirst(RootContainer container, Iterable<ASlot> slots) {
		for (ASlot slot : slots) {
			this.linkFirst(container, slot);
		}
	}

	/**
	 * @see #link(RootContainer, ASlot) but max priority (inserted at begining of list)
	 */
	public void linkFirst(RootContainer container, ASlot slot) {
		this.targetSlotIds.computeIfAbsent(container, a -> new ArrayList<>()).add(0, slot);
	}

	public void linkAll(RootContainer container, Iterable<ASlot> slots) {
		for (ASlot slot : slots) {
			this.link(container, slot);
		}
	}

	/**
	 * todo add shift click compat with non-standard GUIs when this slot is shift-clicked this method is called
	 */
	public void link(RootContainer container, ASlot slot) {
		this.targetSlotIds.computeIfAbsent(container, a -> new ArrayList<>()).add(slot);
	}

	@Override
	protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
		this.renderBackground(graphics, tickDelta);
		if (this.render) {
			graphics.drawItem(this.override == null ? this.getStack() : this.override);
			if (this.highlighted) {
				this.renderGradient(graphics, tickDelta);
				if(!this.isTooltipHandled(container)) {
					graphics.drawTooltipAutowrap(this.getStack());
				}
			}
		}
	}

	@Override
	public boolean isValid(RootContainer container, PlayerEntity entity) {
		return this.inventory.canPlayerUse(entity);
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		this.writeInventoryData(output, this.inventory);
		output.putInt("index", this.index);
		output.putInt("overrideClient", this.minecraftSlots.get(container).id);
		if (this.targetSlotIds.containsKey(container)) {
			output.put("targetSlotIds", NBTType.INT_ARRAY, this.targetSlotIds.get(container).stream().map(ADrawable::getSyncId).collect(Collectors.toCollection(IntArrayList::new)));
		}
	}

	/**
	 * write any data required to find the corresponding inventory on the other side (client)
	 */
	protected abstract void writeInventoryData(NBTagView.Builder output, Inventory inventory);

	@Override
	protected void onAdded(RootContainer container) {
		super.onAdded(container);
		Validate.isTrue(container instanceof SlotAddAccess, "cannot add slot to non-handled screens!");
		MinecraftSlot slot = new MinecraftSlot(container);
		this.minecraftSlots.put(container, slot);
		if (this.isClient()) {
			if (this.overrideClient != -1) {
				slot.override = this.overrideClient;
				slot.id = this.overrideClient;
			}
		}

		if (this.temp != null) {
			List<ASlot> list = new ArrayList<>();
			for (int i = 0; i < this.temp.size(); i++) {
				int val = this.temp.getInt(i);
				ASlot target = (ASlot) container.forId(val);
				list.add(target);
			}
			this.temp = null;
			this.targetSlotIds.put(container, list);
		}
		((SlotAddAccess) container).addSlot(slot);
	}

	protected void renderBackground(GuiGraphics graphics, float tickDelta) {
		graphics.drawSprite(SLOT);
	}

	public ItemStack getStack() {
		return this.inventory.getStack(this.index);
	}

	protected void renderGradient(GuiGraphics graphics, float tickDelta) {
		try(Close c = graphics.translate(1, 1)) {
			graphics.highlightRectangle(16, 16);
		}
	}

	public void setStack(ItemStack stack) {
		this.inventory.setStack(this.index, stack);
	}

	public boolean isTooltipHandled(RootContainer container) {
		return this.minecraftSlots.containsKey(container);
	}

	/**
	 * Transfers the contents of this slot into wherever you want to move the items
	 *
	 * @return the itemstack that was transfered
	 */
	@Environment (EnvType.CLIENT)
	public ItemStack quickTransferStack(RootContainer container) {
		if (this.slotIds == null) {
			this.slotIds = new IntArrayList();
			if(this.targetSlotIds.get(container) == null) {
				throw new IllegalStateException("Slot was not linked (" + this + ") https://github.com/Astrarre/Astrarre/wiki/Astrarre-Gui-v0#adding-inventory-slots");
			}
			for (ASlot slot : this.targetSlotIds.get(container)) {
				this.slotIds.add(slot.minecraftSlots.get(container).id);
			}
		}
		IntList ids = this.slotIds;
		if (this.getStack().isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = this.getStack();
		boolean failure = false;
		for (int i = 0; i < ids.size(); i++) {
			int slot = ids.getInt(i);
			ScreenHandler handler = ((ScreenHandlerContainer) container).handler;
			if (((ScreenHandlerAccess) handler).callInsertItem(stack, slot, slot + 1, false)) {
				failure = true;
			}
		}
		if (!failure) {
			return ItemStack.EMPTY;
		}

		return stack.copy();
	}

	@Override
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		return true;
	}

	private class MinecraftSlot extends net.minecraft.screen.slot.Slot implements ExtraSlotAccess {
		public final RootContainer container;
		public int override = -1;

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
		public ItemStack transferSlot(RootContainer container) {
			return ASlot.this.quickTransferStack(container);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return this.inventory.isValid(ASlot.this.index, stack);
		}
	}
}

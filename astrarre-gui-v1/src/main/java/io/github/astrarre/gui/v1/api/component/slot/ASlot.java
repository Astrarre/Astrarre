package io.github.astrarre.gui.v1.api.component.slot;

import java.awt.geom.Point2D;
import java.util.Optional;

import io.github.astrarre.gui.internal.ElementRootPanel;
import io.github.astrarre.gui.internal.NullSlot;
import io.github.astrarre.gui.internal.mixin.ScreenHandlerAccess;
import io.github.astrarre.gui.internal.mixin.SlotAccess;
import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.component.AComponent;
import io.github.astrarre.gui.v1.api.component.ARootPanel;
import io.github.astrarre.gui.v1.api.component.ToggleableComponent;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.server.ServerPanel;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.space.item.ModelTransformType;
import io.github.astrarre.rendering.v1.api.util.Point2f;
import io.github.astrarre.util.v0.api.Lazy;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// todo bidirectional compat needed for modifying vanilla GUIs

/**
 * To setup a slot, you must create a Key, this key should be created in common code (server & client). You must link the slots on the client and
 * server {@link ASlotHelper#linkFromServer(PacketHandler, ServerPanel, SlotKey)}. And you must link the key itself for shift-click-transfer,
 * eg. what slots to move the items to when a player shift-clicks on your slot. {@link SlotKey#link(SlotKey)}
 */
public class ASlot extends AComponent implements ToggleableComponent {
	private static final Icon DEFAULT = Icon.slot(18, 18);
	final ElementRootPanel.ScreenImpl screen;
	final PacketHandler communication;
	final SlotKey key;
	final Lazy<?> resetPos;
	Slot slot;
	ItemStack toRender = ItemStack.EMPTY;
	boolean highlightOverride;
	private Icon icon;

	/**
	 * @param key {@link ASlotHelper}
	 */
	public ASlot(PacketHandler communication, ARootPanel screen, SlotKey key) {
		this(communication, screen, key, DEFAULT);
	}

	/**
	 * @param key {@link ASlotHelper}
	 */
	public ASlot(PacketHandler communication, ARootPanel screen, SlotKey key, Icon icon) {
		this.screen = Validate.instanceOf(screen, ElementRootPanel.ScreenImpl.class, "Can only use slots on screens");
		this.communication = communication;
		this.key = key;
		this.setIcon(icon);
		communication.listen(key, builder -> {
			HandledScreen s = (HandledScreen) this.screen.element;
			ScreenHandler handler = s.getScreenHandler();
			int slotId = builder.getInt("index");
			while(handler.slots.size() <= slotId) {
				((ScreenHandlerAccess) handler).callAddSlot(NullSlot.INSTANCE);
			}

			this.slot = new Minecraft(key.inventory, key.slotIndex, key, this);
			this.slot.id = slotId;
			handler.slots.set(slotId, this.slot);
		});

		this.resetPos = Lazy.init(this::updateLocation);
	}

	/**
	 * this isn't guaranteed to do anything, but as of minecraft 1.17, slots have an x and y position, it's not completely nessesary,
	 * but certain mods may rely on it, so this method tries it's best to set that position
	 */
	public void updateLocation() {
		if(this.slot == null) return;
		Point2D.Float point = new Point2D.Float(-1024, -1024);
		this.screen.find(this, t -> { // if packet arives first this doesn't work bruh
			point.setLocation(0, 0);
			t.transform().transform(point);
			return true;
		});
		((SlotAccess)this.slot).setX((int) point.x);
		((SlotAccess)this.slot).setY((int) point.y);
	}

	public Icon getIcon() {
		return this.icon;
	}

	public ASlot setIcon(@NotNull Icon icon) {
		this.icon = icon;
		this.lockBounds(false);
		this.setBounds(icon.width(), icon.height());
		this.lockBounds(true);
		return this;
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		this.resetPos.get();

		this.icon.render(render);
		if(this.slot != null) {
			ItemStack stack = this.toRender; // updated by minecraft
			if(!stack.isEmpty()) {
				try(var ignore = render.scale(this.icon.width() / 18f, this.icon.height() / 18f)) {
					try(var ignore1 = render.translate(1, 1)) {
						render.item().render(ModelTransformType.Standard.GUI, stack);
					}
				}
			}
		}
		if(this.highlightOverride || this.isIn(cursor)) { // we need both because I skip minecraft's highlight check
			try(var ignore = render.translate(0, 0, 250)) {
				render.fill().rect(0x80ffffff, 1, 1, this.icon.width() - 2, this.icon.height() - 2);
			}
		}
	}

	public static class Minecraft extends Slot {
		final SlotKey key;
		final Object this_;

		public Minecraft(Inventory inventory, int index, SlotKey key) {
			this(inventory, index, key, null);
		}

		public Minecraft(Inventory inventory, int index, SlotKey key, Object this_) {
			super(inventory, index, -1024, -1024);
			this.key = key;
			this.this_ = this_;
		}

		@Override
		public boolean isEnabled() {
			return this.this_ instanceof ASlot a && a.isEnabled();
		}

		@Environment(EnvType.CLIENT)
		public ASlot slot() {
			return (ASlot) this.this_;
		}

		public ItemStack transferToLinked() {
			return this.key.transferToLinked();
		}

		@Override
		public ItemStack getStack() {
			return this.key.getStack();
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return this.key.insert(ItemKey.ofStack(stack), stack.getCount(), true) != 0;
		}

		@Override
		public void setStack(ItemStack stack) {
			this.key.setStack(stack);
			this.markDirty();
		}

		@Override
		public void markDirty() {
			this.key.markDirty();
		}

		@Override
		public int getMaxItemCount() {
			return this.key.getMaxCount(ItemKey.EMPTY);
		}

		@Override
		public int getMaxItemCount(ItemStack stack) {
			return this.key.getMaxCount(ItemKey.ofStack(stack));
		}

		@Override
		public ItemStack takeStack(int amount) {
			ItemKey key = ItemKey.ofStack(this.getStack());
			int count = this.key.extract(key, amount, false);
			return key.createItemStack(count);
		}

		@Override
		public ItemStack insertStack(ItemStack stack, int count) {
			int c = this.key.insert(ItemKey.ofStack(stack), count, false);
			stack.decrement(c);
			return stack;
		}

		@Override
		public boolean canTakeItems(PlayerEntity playerEntity) {
			return this.key.extract(ItemKey.ofStack(this.getStack()), 1, true) != 0;
		}
	}
}

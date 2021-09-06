package io.github.astrarre.gui.v1.api.component;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.internal.ElementRootPanel;
import io.github.astrarre.gui.internal.NullSlot;
import io.github.astrarre.gui.internal.mixin.ScreenHandlerAccess;
import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.comms.PacketKey;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.server.ServerPanel;
import io.github.astrarre.hash.v0.api.Hasher;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.space.item.ModelTransformType;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// todo shift clicking, bidirectional compat needed for modifying vanilla GUIs
public class ASlot extends AComponent implements ToggleableComponent {
	private static final Icon DEFAULT = Icon.slot(18, 18);
	public final ElementRootPanel.ScreenImpl screen;
	public final PacketHandler communication;
	private Icon icon;
	@ApiStatus.Internal
	public Slot slot;

	public ASlot(PacketHandler communication, ARootPanel screen, Key key) {
		this(communication, screen, key, DEFAULT);
	}

	public ASlot(PacketHandler communication, ARootPanel screen, Key key, Icon icon) {
		this.screen = Validate.instanceOf(screen, ElementRootPanel.ScreenImpl.class, "Can only use slots on screens");
		this.communication = communication;
		this.setIcon(icon);
		communication.listen(key, builder -> {
			HandledScreen s = (HandledScreen) this.screen.element;
			ScreenHandler handler = s.getScreenHandler();
			int slotId = builder.getInt("index");
			while(handler.slots.size() <= slotId) {
				((ScreenHandlerAccess)handler).callAddSlot(NullSlot.INSTANCE);
			}
			this.slot = new Minecraft(key.inventory, key.slotIndex, this);
			this.slot.id = slotId;
			handler.slots.set(slotId, this.slot);
		});
	}

	public static void linkAll(PacketHandler handler, ServerPanel panel, List<Key> keys) {
		for(Key key : keys) {
			linkFromServer(handler, panel, key);
		}
	}

	public static void linkFromServer(PacketHandler packet, ServerPanel panel, Key key) {
		ScreenHandler handler = panel.screenHandler();
		packet.sendInfo(key, builder -> {
			var mc = new Minecraft(key.inventory, key.slotIndex, null);
			((ScreenHandlerAccess)handler).callAddSlot(mc);
			builder.putInt("index", mc.id);
		});
	}

	public ASlot setIcon(@NotNull Icon icon) {
		this.icon = icon;
		this.lockBounds(false);
		this.setBounds(icon.width(), icon.height());
		this.lockBounds(true);
		return this;
	}

	public Icon getIcon() {
		return this.icon;
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		this.icon.render(render);
		if(this.slot != null) {
			ItemStack stack = this.slot.getStack();
			if(!stack.isEmpty()) {
				try(var ignore = render.scale(this.icon.width() / 18f, this.icon.height() / 18f)) {
					try(var ignore1 = render.translate(1, 1)) {
						render.item().render(ModelTransformType.Standard.GUI, stack);
					}
				}
			}
		}
		if(this.isIn(cursor)) {
			try(var ignore = render.translate(0, 0, 250)) {
				render.fill().rect(0x80ffffff, 1, 1, this.icon.width() - 2, this.icon.height() - 2);
			}
		}
	}

	public static class Minecraft extends Slot {
		final Object this_;
		public Minecraft(Inventory inventory, int index, Object this_) {
			super(inventory, index, -1024, -1024);
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
	}

	private static final Id ID = Id.create("astrarre", "slot_key");

	public static List<Key> inv(Inventory inventory, int inventoryId) {
		return inv(inventory, 0, inventory.size(), inventoryId);
	}

	public static List<Key> inv(Inventory inventory, int from, int len, int inventoryId) {
		List<Key> keys = new ArrayList<>(inventory.size());
		for(int i = 0; i < len; i++) {
			keys.add(new Key(inventory, inventoryId, i + from));
		}
		return keys;
	}

	public static class Key extends PacketKey {
		final Inventory inventory;
		final int inventoryId;
		final int slotIndex;

		/**
		 * @param inventory the inventory, does not actually have to be an updated view, this is primarily used for it's isValid method
		 * @param inventoryId a unique int id for this inventory, this is used to link the inventory to it's serverside counterpart
		 * @param slotIndex the slot (index in inventory) this slot represents
		 */
		public Key(Inventory inventory, int inventoryId, int slotIndex) {
			super(ID);
			this.inventory = inventory;
			this.inventoryId = inventoryId;
			this.slotIndex = slotIndex;
		}

		@Override
		protected void hash0(Hasher hasher) {
			hasher.putInt(this.inventoryId);
			hasher.putInt(this.slotIndex);
		}
	}
}

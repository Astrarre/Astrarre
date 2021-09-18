package io.github.astrarre.gui.v1.api.component.slot;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import io.github.astrarre.gui.internal.ElementRootPanel;
import io.github.astrarre.gui.internal.slot.NullSlot;
import io.github.astrarre.gui.internal.mixin.ScreenHandlerAccess;
import io.github.astrarre.gui.internal.mixin.SlotAccess;
import io.github.astrarre.gui.internal.slot.SlotAdapter;
import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.component.AGrid;
import io.github.astrarre.gui.v1.api.component.AHoverableComponent;
import io.github.astrarre.gui.v1.api.component.AList;
import io.github.astrarre.gui.v1.api.component.APanel;
import io.github.astrarre.gui.v1.api.component.ARootPanel;
import io.github.astrarre.gui.v1.api.component.ToggleableComponent;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.server.ServerPanel;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.space.item.ModelTransformType;
import io.github.astrarre.rendering.v1.api.util.Axis2d;
import io.github.astrarre.util.v0.api.Lazy;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

// todo bidirectional compat needed for modifying vanilla GUIs

/**
 * To setup a slot, you must create a Key, this key should be created in common code (server & client). You must link the slots on the client and
 * server {@link SlotKey#sync(PacketHandler, ServerPanel)}. And you must link the key itself for shift-click-transfer,
 * eg. what slots to move the items to when a player shift-clicks on your slot. {@link SlotKey#link(SlotKey)}.
 *
 * You can technically use this on the client, however you wont be able to extract/insert from the inventory (for obvious reasons).
 */
public class ASlot extends AHoverableComponent implements ToggleableComponent {
	private static final Icon DEFAULT = Icon.slot(18, 18);
	final ARootPanel screen;
	final PacketHandler communication;
	final SlotKey key;
	final Lazy<?> resetPos;
	Slot slot;
	ItemStack toRender;
	boolean highlightOverride;
	private Icon icon;

	/**
	 * @param key {@link SlotKey#link(SlotKey)}
	 */
	public ASlot(PacketHandler communication, ARootPanel screen, SlotKey key) {
		this(communication, screen, key, DEFAULT);
	}

	/**
	 * @param key {@link SlotKey#link(SlotKey)}
	 */
	public ASlot(PacketHandler communication, ARootPanel screen, SlotKey key, Icon icon) {
		this.communication = communication;
		this.key = key;
		this.setIcon(icon);
		this.screen = screen;
		if(screen instanceof ElementRootPanel.ScreenImpl impl) {
			communication.listen(key, builder -> {
				HandledScreen s = (HandledScreen) impl.element;
				ScreenHandler handler = s.getScreenHandler();
				int slotId = builder.getInt("index");
				while(handler.slots.size() <= slotId) {
					((ScreenHandlerAccess) handler).callAddSlot(NullSlot.INSTANCE);
				}

				this.slot = new SlotAdapter(key.inventory, key.slotIndex, key, this);
				this.slot.id = slotId;
				handler.slots.set(slotId, this.slot);
			});

			this.resetPos = Lazy.init(this::updateLocation);
		} else {
			this.resetPos = null;
		}
	}

	/**
	 * Creates a panel with player inventory slots
	 * @param player {@link SlotKey#player(PlayerInventory, int)}
	 */
	public static APanel playerInv(PacketHandler handler, ARootPanel panel, List<SlotKey> player) {
		AList playerInv = new AList(Axis2d.Y); // 68 pixels tall

		AGrid grid = new AGrid(16, 9, 3);
		for(int row = 0; row < 3; row++) {
			for(int column = 0; column < 9; column++) { // 144 pixels wide
				int index = 9 + (row * 9) + column;
				grid.add(new ASlot(handler, panel, player.get(index)));
			}
		}

		AList hotbar = new AList(Axis2d.X);
		for(int i = 0; i < 9; i++) {
			hotbar.add(new ASlot(handler, panel, player.get(i)));
		}

		return playerInv;
	}

	/**
	 * this isn't guaranteed to do anything, but as of minecraft 1.17, slots have an x and y position, it's not completely nessesary,
	 * but certain mods may rely on it, so this method tries it's best to set that position. This is only relavent when using slots server-side
	 */
	public void updateLocation() {
		if(this.slot != null) {
			Point2D.Float point = new Point2D.Float(-1024, -1024);
			this.screen.find(this, t -> { // if packet arives first this doesn't work bruh
				point.setLocation(0, 0);
				t.transform().transform(point);
				return true;
			});
			((SlotAccess) this.slot).setX((int) point.x);
			((SlotAccess) this.slot).setY((int) point.y);
		}
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
		this.icon.render(render);
		if(this.slot != null) {
			this.resetPos.get();
		}

		ItemStack stack = Validate.orDefault(this.toRender, this.key.getStack());
		if(!stack.isEmpty()) {
			try(var ignore = render.scale(this.icon.width() / 18f, this.icon.height() / 18f)) {
				try(var ignore1 = render.translate(1, 1)) {
					render.item().render(ModelTransformType.Standard.GUI, stack);
				}
			}
		}

		if(this.highlightOverride || this.isIn(cursor)) { // we need both because I skip minecraft's highlight check
			try(var ignore = render.translate(0, 0, 250)) {
				render.fill().rect(0x80ffffff, 1, 1, this.icon.width() - 2, this.icon.height() - 2);
			}
		}
	}
}

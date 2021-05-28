package io.github.astrarre.gui.v0.api.container;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.AAggregateDrawable;
import io.github.astrarre.gui.v0.api.base.borders.ABeveledBorder;
import io.github.astrarre.gui.v0.api.base.panel.ACenteringPanel;
import io.github.astrarre.gui.v0.api.base.panel.APanel;
import io.github.astrarre.gui.v0.api.base.statik.ADarkenedBackground;
import io.github.astrarre.gui.v0.fabric.adapter.slot.APlayerSlot;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ASlot;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.rendering.v0.api.Transformation;

import net.minecraft.entity.player.PlayerInventory;

public abstract class ContainerGUI {
	/*the number of pixels from the bottom of the panel to the first slot (hotbar)*/
	private static final int PIXELS_TO_INVENTORY_BOTTOM = 7;
	/*the number of pixels from the bottom of the main inventory area to the hotbar row*/
	private static final int PIXELS_FROM_MAIN_INVENTORY_TO_HOTBAR_ROW = 4;
	/*the size of a slot in pixels*/
	private static final int SLOT_WIDTH = 18;
	/*the length of a row in the inventory*/
	private static final int INVENTORY_ROW_LENGTH = 9;

	/**
	 * the width of the inventory part of the container
	 */
	public static final int INVENTORY_WIDTH = SLOT_WIDTH * 9;
	public static final int INVENTORY_HEIGHT = 4 * SLOT_WIDTH + PIXELS_FROM_MAIN_INVENTORY_TO_HOTBAR_ROW + PIXELS_TO_INVENTORY_BOTTOM;

	protected final RootContainer container;
	protected final NetworkMember member;

	protected final int width, height;

	/**
	 * @see RootContainer#openContainer(NetworkMember, BiFunction)
	 */
	public ContainerGUI(RootContainer container, NetworkMember member, int width, int height) {
		this.container = container;
		this.member = member;
		this.width = width;
		this.height = height;
	}

	/**
	 * called when the gui opens (kinda like a post-constructor thing)
	 */
	public void initContainer() {
		APanel contentPanel = this.container.getContentPanel();
		contentPanel.add(this.getBackground());
		AAggregateDrawable mainPanel = this.getMainPanel();
		List<ASlot> slots = this.addPlayerInventory(mainPanel);
		contentPanel.add(this.getPanelBackground(mainPanel));
		this.addGui(mainPanel, this.getContainerWidth(), this.getContainerHeight() - INVENTORY_HEIGHT, slots);
	}

	/**
	 * create the actual gui
	 * @param playerSlots depending on the API (eg. REI compatibility) this may be empty
	 */
	protected abstract void addGui(AAggregateDrawable panel, int width, int height, List<ASlot> playerSlots);

	/**
	 * @return all the 'inventory slots' (anything you could reasonably shift-click from/to)
	 */
	protected List<ASlot> addPlayerInventory(AAggregateDrawable mainPanel) {
		int inventoryStartX = this.getContainerWidth() / 2 - (INVENTORY_ROW_LENGTH * SLOT_WIDTH) / 2, inventoryStartY = this.getContainerHeight() - (PIXELS_TO_INVENTORY_BOTTOM + (4 * SLOT_WIDTH) + 4);
		PlayerInventory inventory = this.member.to().getInventory();

		List<ASlot> mainInventorySlots = new ArrayList<>(27);
		for (int slotRow = 0; slotRow < 3; slotRow++) {
			for (int slotColumn = 0; slotColumn < 9; slotColumn++) {
				ASlot slot = new APlayerSlot(inventory, slotColumn + (slotRow * 9));
				slot.setTransformation(Transformation.translate(inventoryStartX + SLOT_WIDTH * slotColumn, inventoryStartY + SLOT_WIDTH * slotRow, 0));
				mainInventorySlots.add(slot);
				mainPanel.add(slot);
			}
		}

		List<ASlot> hotbarSlots = new ArrayList<>(9);

		for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
			ASlot slot = new APlayerSlot(inventory, hotbarSlot + 27);
			slot.setTransformation(Transformation.translate(inventoryStartX + SLOT_WIDTH * hotbarSlot, inventoryStartY + SLOT_WIDTH * 3 + PIXELS_FROM_MAIN_INVENTORY_TO_HOTBAR_ROW, 0));
			hotbarSlots.add(slot);
			mainPanel.add(slot);
			slot.linkAll(this.container, mainInventorySlots);
			for (ASlot mainSlot : mainInventorySlots) {
				mainSlot.link(this.container, slot);
			}
		}

		List<ASlot> combined = new ArrayList<>(36);
		combined.addAll(hotbarSlots);
		combined.addAll(mainInventorySlots);
		return combined;
	}

	protected AAggregateDrawable getMainPanel() {
		return new ACenteringPanel(this.getContainerWidth(), this.getContainerHeight());
	}

	/**
	 * the gui's background (the white beveled part)
	 * @return this is added in leu of the main panel
	 */
	protected ADrawable getPanelBackground(AAggregateDrawable mainPanel) {
		return new ABeveledBorder(mainPanel);
	}

	protected ADrawable getBackground() {
		return new ADarkenedBackground();
	}

	public int getContainerWidth() {
		return this.width;
	}

	public int getContainerHeight() {
		return this.height;
	}
}

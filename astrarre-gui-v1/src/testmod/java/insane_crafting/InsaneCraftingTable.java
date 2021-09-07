package insane_crafting;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Iterables;
import io.github.astrarre.gui.internal.NullInventory;
import io.github.astrarre.gui.v1.api.component.ACenteringPanel;
import io.github.astrarre.gui.v1.api.component.AGrid;
import io.github.astrarre.gui.v1.api.component.AIcon;
import io.github.astrarre.gui.v1.api.component.AList;
import io.github.astrarre.gui.v1.api.component.slot.ASlot;
import io.github.astrarre.gui.v1.api.component.slot.ASlotHelper;
import io.github.astrarre.gui.v1.api.component.slot.SlotKey;
import io.github.astrarre.gui.v1.api.server.ServerPanel;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.plane.icon.backgrounds.StandardBackgroundIcon;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import io.github.astrarre.rendering.v1.api.util.Axis2d;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InsaneCraftingTable extends Block {
	public InsaneCraftingTable(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		openGui(player);
		return ActionResult.CONSUME;
	}

	public static void openGui(PlayerEntity entity) {
		InsaneInventory inventory = new InsaneInventory();
		Arrays.setAll(inventory.inventory, value -> new ItemStack(Items.STICK));

		List<SlotKey> inv = ASlotHelper.inv(inventory, 1), player = ASlotHelper.player(entity, 0);
		CraftingKey key = new CraftingKey(NullInventory.INVENTORY, 2, 0, inventory);

		ServerPanel.openHandled(entity, (communication, panel) -> {
			Icon tileIcon = Icon.slot(8, 8);
			panel.darkBackground(true);
			// construct player inventory
			AList playerInv = new AList(Axis2d.Y); // 68 pixels tall

			AGrid grid = new AGrid(8, 9, 3);
			for(int row = 0; row < 3; row++) {
				for(int column = 0; column < 9; column++) { // 144 pixels wide
					int index = 9 + (row * 9) + column;
					grid.add(new ASlot(communication, panel, player.get(index), tileIcon));
				}
			}

			AList hotbar = new AList(Axis2d.X);
			for(int i = 0; i < 9; i++) {
				hotbar.add(new ASlot(communication, panel, player.get(i), tileIcon));
			}

			playerInv.add(grid);
			playerInv.add(hotbar.with(Transform3d.translate(0, 4, 0)));

			// construct block's inventory
			AGrid main = new AGrid(8, 8, 39, 24);
			for(int i = 0; i < 936; i++) {
				main.add(new ASlot(communication, panel, inv.get(i), tileIcon));
			}

			// side inventories
			AGrid left = new AGrid(8, 8, 14, 5);
			for(int i = 0; i < 70; i++) {
				left.add(new ASlot(communication, panel, inv.get(i + 936), tileIcon));
			}

			AGrid right = new AGrid(8, 8, 14, 5);
			for(int i = 0; i < 70; i++) {
				right.add(new ASlot(communication, panel, inv.get(i + 1006), tileIcon));
			}

			ASlot output = new ASlot(communication, panel, key, tileIcon.colored(0x77FF7700));

			float width = 320, height = 240;
			ACenteringPanel center = new ACenteringPanel(panel, width, height);
			StandardBackgroundIcon background = new StandardBackgroundIcon(width, height);
			center.add(new AIcon(background));
			AList list = new AList(Axis2d.Y);
			list.add(main);
			list.add(playerInv.with(Transform3d.translate((width - 72) / 2 - 10, 2, 0))); // 2 pixel margin

			center.add(list.with(Transform3d.translate(4, 4, 0)));
			center.add(left.with(Transform3d.translate(4, 4 + 192, 0)));
			center.add(right.with(Transform3d.translate(4 + 25 * 8, 4 + 192, 0)));
			center.add(output.with(Transform3d.translate(194, 212, 0)));
			panel.add(center);
		}, (communication, panel) -> {
			ASlotHelper.linkAllFromServer(communication, panel, inv);
			ASlotHelper.linkAllFromServer(communication, panel, player);
			ASlotHelper.linkFromServer(communication, panel, key);
		});
	}

	public static class CraftingKey extends SlotKey {
		final InsaneInventory insane;
		ItemStack previewStack;

		public CraftingKey(Inventory inventory, int inventoryId, int slotIndex, InsaneInventory insane) {
			super(inventory, inventoryId, slotIndex);
			this.insane = insane;
			insane.change.andThen(() -> this.previewStack = null);
		}

		@Override
		public ItemStack getStack() {
			if(this.previewStack == null) this.computePreviewStack();
			return this.previewStack;
		}

		@Override
		public void setStack(ItemStack stack) {
			System.err.println("Attempted to set stack in crafting slot!");
		}

		@Override
		public int extract(ItemKey key, int count, boolean simulate) {
			if(key.isEmpty() || count == 0) return 0;
			ItemStack current = this.getStack();
			if(!current.isEmpty() && key.isEqual(current)) {
				int toExtract = Math.min(current.getCount(), count);
				if(!simulate) {
					for(int i = 0; i < this.insane.size(); i++) {
						this.insane.getStack(i).decrement(toExtract);
					}
					current.decrement(toExtract);
				}
				return toExtract;
			}
			return 0;
		}

		@Override
		public int insert(ItemKey key, int count, boolean simulate) {
			return 0;
		}

		void computePreviewStack() {
			outer:
			for(InsaneCraftingRecipe recipe : InsaneCrafting.RECIPES) { // todo test recipe
				for(int i = 0; i < this.insane.size(); i++) {
					if(!recipe.input.get(i).test(this.insane.getStack(i))) {
						continue outer;
					}
				}

				this.previewStack = recipe.output.copy();
				return;
			}
			this.previewStack = ItemStack.EMPTY;
		}
	}
}

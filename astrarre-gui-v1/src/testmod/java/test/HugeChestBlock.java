package test;

import java.util.List;

import io.github.astrarre.gui.v1.api.component.ACenteringPanel;
import io.github.astrarre.gui.v1.api.component.AIcon;
import io.github.astrarre.gui.v1.api.component.AList;
import io.github.astrarre.gui.v1.api.component.ASlot;
import io.github.astrarre.gui.v1.api.server.ServerPanel;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.plane.icon.backgrounds.StandardBackgroundIcon;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import io.github.astrarre.rendering.v1.api.util.Axis2d;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HugeChestBlock extends BarrelBlock {
	public HugeChestBlock(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if(blockEntity instanceof BarrelBlockEntity t) {
			Icon tileIcon = Icon.slot(8, 8);
			List<ASlot.Key> playerKeys = ASlot.inv(player.getInventory(), 0, 36, 0), inventoryKeys = ASlot.inv(t, 1);
			ServerPanel.openHandled(player, (communication, panel) -> {
				// construct player inventory
				AList playerInv = new AList(Axis2d.Y); // 68 pixels tall
				for(int row = 0; row < 3; row++) {
					AList list = new AList(Axis2d.X);
					for(int column = 0; column < 9; column++) { // 144 pixels wide
						int index = 9 + (row * 9) + column;
						list.add(new ASlot(communication, panel, playerKeys.get(index), tileIcon));
					}
					playerInv.add(list);
				}

				AList hotbar = new AList(Axis2d.X);
				for(int i = 0; i < 9; i++) {
					hotbar.add(new ASlot(communication, panel, playerKeys.get(i), tileIcon));
				}
				playerInv.add(hotbar.with(Transform3d.translate(0, 4, 0)));

				// construct block's inventory
				AList tileInv = new AList(Axis2d.Y);
				for(int row = 0; row < 24; row++) { // 64 pixels tall
					AList list = new AList(Axis2d.X);
					for(int column = 0; column < 39; column++) { // 256 pixels wide
						int index = (row * 39) + column;
						list.add(new ASlot(communication, panel, inventoryKeys.get(index), tileIcon));
					}
					tileInv.add(list);
				}

				float width = 320, height = 240;
				ACenteringPanel center = new ACenteringPanel(panel, width, height);
				StandardBackgroundIcon background = new StandardBackgroundIcon(width, height);
				center.add(new AIcon(background));
				AList list = new AList(Axis2d.Y);
				list.add(tileInv);
				list.add(playerInv.with(Transform3d.translate((width - 72) / 2 - 8, 2, 0)));
				center.add(list.with(Transform3d.translate(4, 4, 0)));
				panel.add(center);
			}, (communication, panel) -> {
				ASlot.linkAll(communication, panel, playerKeys);
				ASlot.linkAll(communication, panel, inventoryKeys);
			});
		}

		return ActionResult.CONSUME;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new Tile(pos, state);
	}

	public static class Tile extends BarrelBlockEntity {
		public Tile(BlockPos pos, BlockState state) {
			super(pos, state);
			this.setInvStackList(DefaultedList.ofSize(this.size(), ItemStack.EMPTY));
		}

		@Override
		public int size() {
			return 936;
		}
	}

}

package insane_crafting;

import java.awt.Point;
import java.util.List;
import java.util.function.IntFunction;

import io.github.astrarre.gui.v1.api.comms.PacketKey;
import io.github.astrarre.gui.v1.api.component.ACenteringPanel;
import io.github.astrarre.gui.v1.api.component.AComponent;
import io.github.astrarre.gui.v1.api.component.AGrid;
import io.github.astrarre.gui.v1.api.component.AIcon;
import io.github.astrarre.gui.v1.api.component.AList;
import io.github.astrarre.gui.v1.api.component.APanel;
import io.github.astrarre.gui.v1.api.component.button.AButton;
import io.github.astrarre.gui.v1.api.component.slot.ASlot;
import io.github.astrarre.gui.v1.api.component.slot.SlotKey;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.server.ServerPanel;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.astrarre.rendering.v1.api.plane.Texture;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.plane.icon.Icons;
import io.github.astrarre.rendering.v1.api.plane.icon.backgrounds.ContainerBackgroundIcon;
import io.github.astrarre.rendering.v1.api.plane.icon.wrapper.MutableIcon;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import io.github.astrarre.rendering.v1.api.util.Axis2d;
import io.github.astrarre.transfer.v0.fabric.inventory.CombinedInventory;
import io.github.astrarre.transfer.v0.fabric.inventory.ForwardingInventory;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Val;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InsaneCraftingTable extends Block implements BlockEntityProvider {
	public InsaneCraftingTable(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(world.getBlockEntity(pos) instanceof Tile t) {
			this.openGui(t, player);
		}
		return ActionResult.CONSUME;
	}

	public void openGui(Tile tile, PlayerEntity entity) {
		InsaneInventory inventory = tile.inventory; // todo local comms nessesary for REI
		PacketKey.Int pkt = new PacketKey.Int(0);

		List<SlotKey> inv = SlotKey.inv(inventory, 1), player = SlotKey.player(entity, 0);
		player.forEach(k -> k.linkAllPre(inv));
		inv.forEach(k -> k.linkAllPre(player));

		SlotKey key = new SlotKey(tile.crafting, 2, 0);
		key.linkAll(player);

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

			ASlot output = new ASlot(communication, panel, key, tileIcon.colored(0x77FF7700));
			var o = new TranslatableText("insane_crafting.output");
			output.tooltipDirect(builder -> builder.text(o, true));

			float width = 320, height = 240;
			ACenteringPanel center = new ACenteringPanel(panel);
			ContainerBackgroundIcon background = new ContainerBackgroundIcon(width, height);
			center.add(new AIcon(background));

			APanel slot = this.create(playerInv, value -> new ASlot(communication, panel, inv.get(value), tileIcon));

			center.add(slot);
			center.add(output.with(Transform3d.translate(194, 226, 0)));

			Texture fillTexture = Texture.create(Id.create("insane_crafting", "textures/icons.png"), 256, 256, 0, 0, 8, 8);
			Point first = new Point(-1, -1);
			MutableIcon icon = new MutableIcon(Icon.item(Items.AIR).asSize(8, 8));
			AButton.State state = new AButton.State(Icons.Groups.button(8, 8).withDisabled(icon).withOverlay(Icon.tex(fillTexture, 8, 8)), c -> {});
			var fillButton = new AButton(state);
			var text = new TranslatableText("insane_crafting.fill");
			fillButton.tooltipDirect(builder -> builder.scrollingText(text, 100, 100, true, true));

			var var = new Val<>(ItemStack.EMPTY);
			var fillRef = new Val<APanel>();
			APanel fill = this.create(playerInv, value -> AButton.button(Icons.Groups.button(8, 8), c -> {
				Point loc = InsaneInventory.pointFromIndex(value);
				if(first.x == -1) {
					first.setLocation(loc);
				} else {
					var builder = NBTagView.builder();
					builder.putInt("fromX", first.x).putInt("fromY", first.y).putInt("toX", loc.x).putInt("toY", loc.y);
					builder.put("stack", FabricSerializers.ITEM_STACK, var.get());
					communication.sendInfo(pkt, builder);
					fillRef.get().disable();
					slot.enable();
					fillButton.enable();
					first.x = -1;
				}
			}));
			fillRef.set(fill);
			fill.disable();

			state.setCallback(c -> {
				ItemStack stack = c.get(Cursor.CURSOR_STACK).copy();
				stack.setCount(1);
				var.set(stack);
				icon.setIcon(Icon.item(stack).asSize(8, 8));
				fill.able(!fill.isEnabled());
				slot.able(!slot.isEnabled());
				fillButton.disable();
			});

			var export = AButton.button(Icons.Groups.button(8, 8), cursor -> {
				String exported = tile.crafting.exportRecipe();
				MinecraftClient.getInstance().keyboard.setClipboard(exported);
			});

			center.add(fillButton.with(Transform3d.translate(194, 214, 0)));
			center.add(export.with(Transform3d.translate(194, 204, 0)));
			center.add(fill);

			panel.add(center);
		}, (communication, panel) -> {
			inv.forEach(k -> k.sync(communication, panel));
			player.forEach(k -> k.sync(communication, panel));
			key.sync(communication, panel);
			communication.listen(pkt, view -> {
				int fromX = view.getInt("fromX"), fromY = view.getInt("fromY"), toX = view.getInt("toX"), toY = view.getInt("toY");
				ItemStack stack = view.get("stack", FabricSerializers.ITEM_STACK);
				for(int x = fromX; x <= toX; x++) {
					for(int y = fromY; y <= toY; y++) {
						int index = InsaneInventory.indexFromPoint(x, y);
						if(index != -1) {
							if(entity.isCreative()) {
								inventory.setStack(index, stack.copy());
							} else {
								int count = Inventories.remove(entity.getInventory(), i -> ItemStack.canCombine(stack, i), stack.getCount(), false);
								if(count != 0 || stack.isEmpty()) {
									ItemStack stk = stack.copy();
									stk.setCount(count);
									entity.getInventory().offerOrDrop(inventory.getStack(index));
									inventory.setStack(index, stk);
								} else {
									return;
								}
							}
						}
					}
				}
			});
		});
	}

	public APanel create(AComponent playerInv, IntFunction<AComponent> component) {
		// construct block's inventory
		AGrid main = new AGrid(8, 8, 39, 24);
		for(int i = 0; i < 936; i++) {
			main.add(component.apply(i));
		}

		// side inventories
		AGrid left = new AGrid(8, 8, 14, 5);
		for(int i = 0; i < 70; i++) {
			left.add(component.apply(i + 936));
		}

		AGrid right = new AGrid(8, 8, 14, 5);
		for(int i = 0; i < 70; i++) {
			right.add(component.apply(i + 1006));
		}

		APanel panel = new APanel();

		AList list = new AList(Axis2d.Y);
		list.add(main);
		list.add(playerInv.with(Transform3d.translate(114, 2, 0))); // 2 pixel margin

		panel.add(list.with(Transform3d.translate(4, 4, 0)));
		panel.add(left.with(Transform3d.translate(4, 4 + 192, 0)));
		panel.add(right.with(Transform3d.translate(4 + 25 * 8, 4 + 192, 0)));
		return panel;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new Tile(pos, state);
	}

	static final class Tile extends BlockEntity implements ForwardingInventory {
		final InsaneInventory inventory = new InsaneInventory();
		final InsaneCraftingSlot crafting = new InsaneCraftingSlot(this.inventory);
		final Inventory realInventory = CombinedInventory.combine(true, this.crafting, this.inventory);

		public Tile(BlockPos pos, BlockState state) {
			super(InsaneCrafting.TILE, pos, state);
			this.inventory.change.andThen(this::markDirty);
		}

		@Override
		public void setWorld(World world) {
			super.setWorld(world);
			this.crafting.world = world;
		}

		@Override
		public void readNbt(NbtCompound nbt) {
			super.readNbt(nbt);
			NbtList nbtList = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
			for(int i = 0, size = nbtList.size(); i < size; i++) {
				NbtElement element = nbtList.get(i);
				ItemStack stack = ItemStack.fromNbt((NbtCompound) element);
				if(!stack.isEmpty()) {
					this.realInventory.setStack(i, stack);
				}
			}
		}

		@Override
		public NbtCompound writeNbt(NbtCompound nbt) {
			NbtList list = new NbtList();
			for(int i = 0; i < this.realInventory.size(); i++) {
				NbtCompound compound = new NbtCompound();
				this.realInventory.getStack(i).writeNbt(compound);
				list.add(compound);
			}
			nbt.put("Items", list);

			return super.writeNbt(nbt);
		}

		@Override
		public Inventory getInventoryDelegate() {
			return this.realInventory;
		}
	}
}

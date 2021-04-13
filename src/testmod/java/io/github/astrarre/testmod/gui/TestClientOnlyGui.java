package io.github.astrarre.testmod.gui;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Tickable;
import io.github.astrarre.gui.v0.api.base.panel.APanel;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Close;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class TestClientOnlyGui {
	private static final NBTType<List<String>> LORE = NBTType.listOf(NBTType.STRING);
	public static void clientOnly() {
		RootContainer container = RootContainer.openClientOnly();
		APanel panel = container.getContentPanel();
		panel.addClient(new TestDrawable().setTransformation(Transformation.translate(100, 50, 0)));
	}

	public static final class TestDrawable extends ADrawable implements Tickable {
		public TestDrawable() {
			super(null);
		}

		@Override
		protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
			ItemStack stack = new ItemStack(Items.SAND);
			NBTagView.Builder tag = (NBTagView.Builder) stack.getOrCreateSubTag("display");
			List<String> lore = new ArrayList<>();
			lore.add(Text.Serializer.toJson(new LiteralText("aaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaa aaaaaa aaaaaaa aaaaaaaaaaa aaaaaaaaaaaaa aaaaaaaaa aaaaaaaaaaaaaaaaaa")));
			lore.add(Text.Serializer.toJson(new LiteralText("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb bbbbbbbbbbbbbbbbbbbbbbbbb bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb bbbbbbbbbbbbbbbb bbbbbbbbbbbbbbbbb bbbbbbbbbbbbbbbbbbbbb bbbbbbb bbbbbbbbb")));
			lore.add(Text.Serializer.toJson(new LiteralText("cccccccccccccccccccccccccccccc ccccccccccccccccccccccccccccccccccccc cccccccccccccccccccc cccccccccccccccccccccccccc ccccccccccccc ccccccc ccccccccc cccccccccccccccccc ccccccccccc")));
			lore.add(Text.Serializer.toJson(new LiteralText("eeeeeeeeeeeeeeeeeee eeeeeeeeee eeeeeeeeeeeeeeeeeeee eeeeeeeeeee eeeeeeeeee eeeeeeeeeeeeeeee eeeeeeeeeeeeeeeeeeeee eeeeeeeeeeeeeeeeeeeeeeeee eeeeeeeeeeeeeeeeee eeeeeeeeeeeeeee eeeeeee")));
			tag.put("Lore", LORE, lore);
			graphics.drawTooltipAutowrap(stack);
			try(Close c = graphics.translate(0, 0, 400)) {
				graphics.fillRect(800, 1f, 0xffffffff);
			}
		}

		@Override
		protected void write0(RootContainer container, NBTagView.Builder output) {
		}

		@Override
		public void tick(RootContainer container) {

		}
	}
}

package io.github.astrarre.gui.v0.fabric.graphics;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.rendering.internal.DummyScreen;
import io.github.astrarre.rendering.v0.fabric.FabricGraphics3d;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class FabricGuiGraphics extends FabricGraphics3d implements GuiGraphics {
	public FabricGuiGraphics(MatrixStack matrices) {
		super(matrices);
	}

	@Override
	public void drawTooltip(List<Text> text) {
		this.pushStage(null);
		DummyScreen.INSTANCE.renderTooltip(this.matrices, text, 0, 0);
		RenderSystem.enableDepthTest();
	}

	@Override
	public void drawOrderedTooltip(List<OrderedText> text) {
		this.pushStage(null);
		DummyScreen.INSTANCE.renderOrderedTooltip(this.matrices, text, 0, 0);
		RenderSystem.enableDepthTest();
	}

	@Override
	public void drawTooltip(ItemStack stack) {
		this.pushStage(null);
		DummyScreen.INSTANCE.renderTooltip(this.matrices, stack, 0, 0);
		RenderSystem.enableDepthTest();
	}

	@Override
	public void drawTooltip(ItemStack stack, int maxWidth) {
		this.pushStage(null);
		List<Text> texts = DummyScreen.INSTANCE.getTooltipFromItem(stack);
		List<OrderedText> orderedTexts = new ArrayList<>();
		for (Text text : texts) {
			orderedTexts.addAll(GuiGraphics.wrap(text, maxWidth));
		}
		DummyScreen.INSTANCE.renderOrderedTooltip(this.matrices, orderedTexts, 0, 0);
		RenderSystem.enableDepthTest();
	}

	@Override
	public void drawItem(ItemKey stack) {
		this.pushStage(null);
		this.getItemRenderer().zOffset = 0;
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(this.matrices.peek().getModel());
		RenderSystem.translatef(0, 0, -150);
		this.getItemRenderer().renderInGui(stack.createItemStack(1), 1, 1);
		RenderSystem.popMatrix();
		RenderSystem.enableDepthTest();
	}

	@Override
	public void drawItem(ItemStack stack) {
		//this.renderGuiItemModel(itemStack, x, y, this.getHeldItemModel(itemStack, (World)null, entity)) 1.17 stuff
		this.pushStage(null);
		this.getItemRenderer().zOffset = 0;
		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(this.matrices.peek().getModel());
		RenderSystem.translatef(0, 0, -140);
		this.getItemRenderer().renderInGui(stack, 1, 1);
		this.getItemRenderer().renderGuiItemOverlay(this.getTextRenderer(), stack, 1, 1);
		RenderSystem.popMatrix();
		RenderSystem.enableDepthTest();
	}
}

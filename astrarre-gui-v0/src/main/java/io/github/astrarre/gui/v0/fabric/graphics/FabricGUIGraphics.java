package io.github.astrarre.gui.v0.fabric.graphics;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.gui.internal.mixin.ScreenAccess;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.rendering.v0.fabric.FabricGraphics3d;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vector4f;

public class FabricGUIGraphics extends FabricGraphics3d implements GuiGraphics {
	public final Screen screen;

	public FabricGUIGraphics(MatrixStack matrices, Screen screen) {
		super(matrices);
		this.screen = screen;
	}

	@Override
	public void drawItem(ItemKey stack) {
		this.pushStage(null);
		this.getItemRenderer().zOffset = 0;
		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.push();
		matrixStack.method_34425(this.matrices.peek().getModel());
		matrixStack.translate(0, 0, -140);
		this.getItemRenderer().renderInGui(stack.createItemStack(1), 1, 1);
		matrixStack.pop();
		RenderSystem.enableDepthTest();
	}

	@Override
	public void drawItem(ItemStack stack) {
		//this.renderGuiItemModel(itemStack, x, y, this.getHeldItemModel(itemStack, (World)null, entity)) 1.17 stuff
		this.pushStage(null);
		this.getItemRenderer().zOffset = 0;
		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.push();
		matrixStack.method_34425(this.matrices.peek().getModel());
		matrixStack.translate(0, 0, -140);
		this.getItemRenderer().renderInGui(stack, 1, 1);
		this.getItemRenderer().renderGuiItemOverlay(this.getTextRenderer(), stack, 1, 1);
		matrixStack.pop();
		RenderSystem.enableDepthTest();
	}

	@Override
	public void drawTooltip(List<Text> text) {
		this.pushStage(null);
		this.screen.renderTooltip(this.matrices, text, 0, 0);
		RenderSystem.enableDepthTest();
	}

	@Override
	public void drawOrderedTooltip(List<OrderedText> text) {
		this.pushStage(null);
		this.screen.renderOrderedTooltip(this.matrices, text, 0, 0);
		RenderSystem.enableDepthTest();
	}

	@Override
	public void drawTooltip(ItemStack stack) {
		this.pushStage(null);
		((ScreenAccess) this.screen).callRenderTooltip(this.matrices, stack, 0, 0);
		RenderSystem.enableDepthTest();
	}

	@Override
	public void drawTooltip(ItemStack stack, int maxWidth) {
		this.pushStage(null);
		List<Text> texts = this.screen.getTooltipFromItem(stack);
		List<OrderedText> orderedTexts = new ArrayList<>();
		for (Text text : texts) {
			orderedTexts.addAll(GuiGraphics.wrap(text, maxWidth));
		}
		this.screen.renderOrderedTooltip(this.matrices, orderedTexts, 0, 0);
		RenderSystem.enableDepthTest();
	}

	@Override
	public void drawTooltipAutowrap(ItemStack stack) {
		// algorithm thing to wrap text even when the matrix stack is rotated
		Vector4f origin = new Vector4f(0, 0, 0, 1);
		origin.transform(this.matrices.peek().getModel());
		float x = origin.getX(), y = origin.getY();
		origin.set(1, 0, 0, 1); // find direction of rotation + scaling stuff
		origin.transform(this.matrices.peek().getModel());
		float deltaX = origin.getX() - x, deltaY = origin.getY() - y;

		Window window = MinecraftClient.getInstance().getWindow();
		float width = window.getScaledWidth();
		float height = window.getScaledHeight();
		float t = (width - x) / deltaX; // parametric equation solved for T
		float hitY = deltaY * t + y; // "max Y coordinate" for test
		int toDrawWidth = 0;
		float maxT;
		if (hitY > height) {
			// bounded by height, height is the maxY
			maxT = (height - y) / deltaY;
		} else {
			// bounded by width
			maxT = t;
		}

		toDrawWidth = (int) Math.ceil(deltaX * maxT);
		this.drawTooltip(stack, toDrawWidth);
	}
}

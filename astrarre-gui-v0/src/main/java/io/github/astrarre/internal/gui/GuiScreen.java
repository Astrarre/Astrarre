package io.github.astrarre.internal.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.v0.api.rendering.util.Closeable;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class GuiScreen extends HandledScreen<GuiScreenHandler> {
	public static final Identifier TEXTURE = new Identifier("astrarre", "textures/gui/default.png");

	static {
		ScreenRegistry.register(GuiScreenHandler.TYPE, GuiScreen::new);
	}

	float test = 0;

	public GuiScreen(GuiScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void init() {
		super.init();
		// Center the title
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		int obw = this.backgroundWidth, obh = this.backgroundHeight;
		int x = (this.width - this.backgroundWidth) / 2;
		int y = (this.height - this.backgroundHeight) / 2;
		this.client.getTextureManager().bindTexture(TEXTURE);
		this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);


		// minimum safe boundary
		this.backgroundHeight = 240;
		this.backgroundWidth = 320;

		x = (this.width - this.backgroundWidth) / 2;
		y = (this.height - this.backgroundHeight) / 2;
		int x2 = x + this.backgroundWidth;
		int y2 = y + this.backgroundHeight;

		// + --
		// |
		this.drawVerticalLine(matrices, x, y, y2, 0xff00ff00);
		this.drawHorizontalLine(matrices, x, x2, y, 0xff00ff00);

		//    |
		// -- +
		this.drawVerticalLine(matrices, x2, y, y2, 0xff0000ff);
		this.drawHorizontalLine(matrices, x, x2, y2, 0xff0000ff);

		this.fillGradient2(new GuiGraphics(matrices, this));
		// width/height isn't the literal size of the screen, it's kind of like a self-simplifying fraction
		//      this basically means, if you stretch both width and height, it self simplifies, so the image just gets scaled up
		//      but if you only scale width, the fraction wont simplify (multiplying the numerator by 2)
		// backgroundWith is the width of the texture
		// x and y are array coordinates, not origin coordinates
		this.backgroundHeight = obh;
		this.backgroundWidth = obw;
	}

	protected void fillGradient2(GuiGraphics graphics) {
		test += .03;
		test %= Math.PI * 2;
		float test = Math.abs(this.test);
		try (Closeable _temp3 = graphics.setOffsetCloseable(100, 100)) {
			try (Closeable _temp2 = graphics.rotate(50, 50, test)) {
				try (Closeable _temp = graphics.rotate(0, 0, test)) {
					graphics.fillGradient(0, 0, 100, 100, 0xffff0000, 0xff00ff00);
				}
				graphics.fillGradient(0, 0, 100, 100, 0xff0000ff, 0xffffff00);
			}
		}

	}

	@Override
	public void fillGradient(MatrixStack matrices, int xStart, int yStart, int xEnd, int yEnd, int colorStart, int colorEnd) {
		super.fillGradient(matrices, xStart, yStart, xEnd, yEnd, colorStart, colorEnd);
	}
}

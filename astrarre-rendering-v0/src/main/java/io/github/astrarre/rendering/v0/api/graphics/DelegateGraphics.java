package io.github.astrarre.rendering.v0.api.graphics;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.textures.SpriteInfo;
import io.github.astrarre.rendering.v0.api.textures.Texture;
import io.github.astrarre.rendering.v0.api.util.Close;

import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class DelegateGraphics implements Graphics3d {
	protected final Graphics3d delegate;

	public static Graphics3d resolve(Graphics3d g3d) {
		while (g3d instanceof DelegateGraphics) {
			g3d = ((DelegateGraphics) g3d).delegate;
		}
		return g3d;
	}

	public DelegateGraphics(Graphics3d delegate) {
		this.delegate = delegate;
	}

	@Override
	public void drawText(String text, int color, boolean shadow) {
		this.delegate.drawText(text, color, shadow);
	}

	@Override
	public void drawText(Text text, int color, boolean shadow) {
		this.delegate.drawText(text, color, shadow);
	}

	@Override
	public void drawText(OrderedText text, int color, boolean shadow) {
		this.delegate.drawText(text, color, shadow);
	}

	@Override
	public void drawSprite(SpriteInfo sprite) {
		this.delegate.drawSprite(sprite);
	}

	@Override
	public void drawTexture(Texture texture, int x1, int y1, int width, int height) {
		this.delegate.drawTexture(texture, x1, y1, width, height);
	}

	@Override
	public void drawLine(float x1, float y1, float x2, float y2, int color) {
		this.delegate.drawLine(x1, y1, x2, y2, color);
	}

	@Override
	public void drawItem(ItemKey stack) {
		this.delegate.drawItem(stack);
	}

	@Override
	public void drawItem(ItemStack stack) {
		this.delegate.drawItem(stack);
	}

	@Override
	public void drawLine(float length, int color) {
		this.delegate.drawLine(length, color);
	}

	@Override
	public void fillRect(float x, float y, float width, float height, int color) {
		this.delegate.fillRect(x, y, width, height, color);
	}

	@Override
	public void fillRect(float width, float height, int color) {
		this.delegate.fillRect(width, height, color);
	}

	@Override
	public void fillGradient(float width, float height, int startColor, int endColor) {
		this.delegate.fillGradient(width, height, startColor, endColor);
	}

	@Override
	public Close applyTransformation(Transformation transformation) {
		return this.delegate.applyTransformation(transformation);
	}
}

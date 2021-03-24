package io.github.astrarre.rendering.v0.api.graphics;

import java.util.List;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.rendering.v0.edge.Stencil;

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
	public void drawLine(float length, int color) {
		this.delegate.drawLine(length, color);
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
	public void drawTooltip(List<Text> text) {
		this.delegate.drawTooltip(text);
	}

	@Override
	public void drawOrderedTooltip(List<OrderedText> text) {
		this.delegate.drawOrderedTooltip(text);
	}

	@Override
	public void drawTooltip(ItemStack stack) {
		this.delegate.drawTooltip(stack);
	}


	@Override
	public void fillRect(float x, float y, float width, float height, int color) {
		this.delegate.fillRect(x, y, width, height, color);
	}

	@Override
	public void fillGradient(float x, float y, float width, float height, int startColor, int endColor) {
		this.delegate.fillGradient(x, y, width, height, startColor, endColor);
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
	public void drawItem(ItemKey stack) {
		this.delegate.drawItem(stack);
	}

	@Override
	public void drawItem(ItemStack stack) {
		this.delegate.drawItem(stack);
	}

	@Override
	public Close applyTransformation(Transformation transformation) {
		return this.delegate.applyTransformation(transformation);
	}

	@Override
	public void flush() {
		this.delegate.flush();
	}

	@Override
	public Stencil stencil() {
		return this.delegate.stencil();
	}

	@Override
	public void drawLine(float x1, float y1, float z1, float x2, float y2, float z2, int color) {
		this.delegate.drawLine(x1, y1, z1, x2, y2, z2, color);
	}

	@Override
	public void drawLine(float x1, float y1, float x2, float y2, int color) {
		this.delegate.drawLine(x1, y1, x2, y2, color);
	}

	@Override
	public void fillRect(float x1,
			float y1,
			float z1,
			float x2,
			float y2,
			float z2,
			float x3,
			float y3,
			float z3,
			float x4,
			float y4,
			float z4,
			int color) {
		this.delegate.fillRect(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, color);
	}

	@Override
	public void tracePolygon(Polygon polygon, int color) {
		this.delegate.tracePolygon(polygon, color);
	}

	@Override
	public void fillPolygon(Polygon polygon, int color) {
		this.delegate.fillPolygon(polygon, color);
	}

	@Override
	public void drawSprite(Sprite sprite, float width, float height) {
		this.delegate.drawSprite(sprite, width, height);
	}

	@Override
	public void drawSprite(Sprite.Sized sized) {
		this.delegate.drawSprite(sized.sprite, sized.width, sized.height);
	}
}

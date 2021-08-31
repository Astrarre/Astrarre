package io.github.astrarre.gui.v1.api.component.icon;

import io.github.astrarre.rendering.v1.api.space.Render3d;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public record LabelIcon(OrderedText text, int argb, boolean shadow) implements Icon {
	@Override
	public float height() {
		return 10;
	}

	@Override
	public float width() {
		return MinecraftClient.getInstance().textRenderer.getWidth(this.text);
	}

	@Override
	public void render(Render3d render) {
		render.text(this.argb, 0, 0, this.shadow).render(this.text);
	}
}

package io.github.astrarre.gui.internal.rei;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.gui.internal.PanelElement;
import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.gui.v0.fabric.graphics.FabricGUIGraphics;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.gui.widget.Widget;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

public class WidgetAdapter extends Widget {
	protected final RootContainerInternal container;
	protected final Rectangle bounds;
	protected final List<Element> elements;

	public WidgetAdapter(RootContainerInternal container, Rectangle bounds) {
		this.container = container;
		this.elements = Collections.singletonList(new RectangleShiftedElement(new PanelElement(container.getContentPanel(), container), bounds));
		this.bounds = bounds;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		RootContainerInternal internal = this.container;
		try {
			boolean enabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
			if (!enabled) {
				RenderSystem.enableDepthTest();
			}
			matrices.push();
			matrices.translate(this.bounds.x, this.bounds.y, 0);
			GuiGraphics g3d = new FabricGUIGraphics(matrices, MinecraftClient.getInstance().currentScreen);
			mouseX -= this.bounds.x;
			mouseY -= this.bounds.y;
			if (mouseX >= 0 && mouseY >= 0 && mouseX <= this.bounds.width && mouseY <= this.bounds.height) {
				internal.getContentPanel().mouseHover(internal, mouseX, mouseY);
			}
			internal.getContentPanel().render(internal, g3d, delta);
			g3d.flush();
			matrices.pop();
			if (!enabled) {
				RenderSystem.disableDepthTest();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public List<? extends Element> children() {
		return this.elements;
	}

	@Nullable
	@Override
	public Element getFocused() {
		return this.elements.get(0);
	}
}

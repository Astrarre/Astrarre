package io.github.astrarre.gui.v1.rei;

import java.util.List;

import io.github.astrarre.gui.internal.ElementRootPanel;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.DisplayRenderer;
import me.shedaniel.rei.api.client.gui.widgets.WidgetHolder;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.util.math.MatrixStack;

public abstract class AstrarreDisplayRenderer extends DisplayRenderer implements WidgetHolder {
	final BasicParent parent = new BasicParent();
	final ElementImpl impl = new ElementImpl(this.parent);

	@Override
	public void render(MatrixStack stack, Rectangle rectangle, int mouseX, int mouseY, float tickDelta) {
		this.impl.render(stack, mouseX, mouseY, tickDelta);
	}

	@Override
	public List<? extends Element> children() {
		return List.of(this.parent);
	}

	class BasicParent implements ParentElement {
		Element focused;
		boolean dragging;

		@Override
		public List<? extends Element> children() {
			return List.of(AstrarreDisplayRenderer.this.impl);
		}

		@Override
		public boolean isDragging() {
			return this.dragging;
		}

		@Override
		public void setDragging(boolean dragging) {
			this.dragging = dragging;
		}

		@Nullable
		@Override
		public Element getFocused() {
			return this.focused;
		}

		@Override
		public void setFocused(@Nullable Element focused) {
			this.focused = focused;
		}
	}

	class ElementImpl extends ElementRootPanel {

		public ElementImpl(ParentElement element) {
			super(element);
		}

		@Override
		protected int width() {
			return AstrarreDisplayRenderer.this.getWidth();
		}

		@Override
		protected int height() {
			return AstrarreDisplayRenderer.this.getHeight();
		}
	}
}

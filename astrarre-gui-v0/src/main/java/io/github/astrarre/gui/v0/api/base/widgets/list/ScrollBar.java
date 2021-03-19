package io.github.astrarre.gui.v0.api.base.widgets.list;

import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Tickable;
import io.github.astrarre.gui.v0.api.base.AggregateDrawable;
import io.github.astrarre.gui.v0.api.base.widgets.AButton;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

public class ScrollBar extends AggregateDrawable implements Tickable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(Id.create("astrarre-gui-v0", "scroll_bar"), ScrollBar::new);
	public final float width, height;
	protected AButton scrollBar, up, down;
	public float fraction = 2, scrollPercent;

	/**
	 * @param height the literal height of the component
	 * @param fraction the 'number of options' (this is used to compute the size of the scrollbar itself)
	 */
	public ScrollBar(float height, float fraction) {
		this(new AButton(AButton.ARROW_UP), new AButton(AButton.ARROW_DOWN), height, fraction);
	}
	public ScrollBar(AButton up, AButton down, float height, float fraction) {
		this(ENTRY, up, down, height);
		this.fraction = fraction;
	}

	protected ScrollBar(DrawableRegistry.Entry id, AButton up, AButton down, float height) {
		super(id);
		this.add(up);
		this.add(down);
		this.width = up.width();
		this.height = height;
		this.setBounds(Polygon.rectangle(this.width, height));
		this.scrollBar = null;
		this.up = up;
		this.down = down;
		down.setTransformation(Transformation.translate(0, height - this.down.height(), 0));
	}

	protected ScrollBar(DrawableRegistry.Entry id, Input input) {
		super(id, input);
		this.width = input.readFloat();
		this.height = input.readFloat();
		this.fraction = input.readFloat();
	}

	@Override
	protected void onAdded(RootContainer container) {
		super.onAdded(container);
		if(container.isClient()) {
			this.up = ((AButton) this.drawables.get(0));
			this.up.onPress(() -> this.onChangeClient(Math.max(this.scrollPercent + 1 / this.fraction, 0)));
			this.down = ((AButton) this.drawables.get(1));
			this.down.onPress(() -> this.onChangeClient(Math.min(this.scrollPercent + 1 / this.fraction, 1)));
			this.addClient(this.scrollBar = new Bar());
		}
	}

	/**
	 * fired when the scroll percentage of the bar changes
	 */
	protected void onChangeClient(float newPercent) {
		this.scrollPercent = newPercent;
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		for (Drawable drawable : this.drawables) {
			drawable.render(container, graphics, tickDelta);
		}
	}

	@Override
	protected void write0(RootContainer container, Output output) {
		super.write0(container, output);
		output.writeFloat(this.width);
		output.writeFloat(this.height);
		output.writeFloat(this.fraction);
	}

	@Override
	public void tick(RootContainer container) {
		this.scrollBar.setTransformation(Transformation.translate(0, this.up.height() + this.scrollPercent * (this.height - this.down.height() - (this.height / this.fraction)), 0));
	}

	public class Bar extends AButton {
		public Bar() {
			super(null);
		}

		@Override
		protected void drawPressed(RootContainer container, Graphics3d graphics, float tickDelta) {
			graphics.fillRect(this.width(), this.height(), 0xffaaaaaa);
			graphics.drawRect(0, 0, this.width(), this.height(), 0xff000000);
		}

		@Override
		protected void drawActive(RootContainer container, Graphics3d graphics, float tickDelta) {
			graphics.fillRect(this.width(), this.height(), 0xffcccccc);
			graphics.drawRect(0, 0, this.width(), this.height(), 0xff000000);
		}

		@Override
		public float width() {
			return ScrollBar.this.width;
		}

		@Override
		public float height() {
			return (ScrollBar.this.height - (ScrollBar.this.up.height() + ScrollBar.this.down.height())) / ScrollBar.this.fraction - 1;
		}

		@Override
		public boolean mouseDragged(RootContainer container, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
			double percent = ScrollBar.this.scrollPercent + deltaY / (1f + Math.abs(deltaY * 10));
			if (percent > 1) {
				percent = 1;
			} else if (ScrollBar.this.scrollPercent < 0) {
				percent = 0;
			}
			ScrollBar.this.onChangeClient((float) percent);
			return true;
		}
	}

	@Override
	public boolean mouseScrolled(RootContainer container, double mouseX, double mouseY, double amount) {
		double percent = this.scrollPercent - amount / (1f + Math.abs(amount * 10));
		if (percent > 1) {
			percent = 1;
		} else if (percent < 0) {
			percent = 0;
		}
		this.onChangeClient((float) percent);
		return true;
	}

	@Override
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		return true;
	}

	public static void init() {}
}

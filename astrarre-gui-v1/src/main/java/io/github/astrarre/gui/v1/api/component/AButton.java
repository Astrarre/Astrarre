package io.github.astrarre.gui.v1.api.component;

import io.github.astrarre.gui.v1.api.AComponent;
import io.github.astrarre.gui.v1.api.FocusableComponent;
import io.github.astrarre.gui.v1.api.component.icon.Icon;
import io.github.astrarre.gui.v1.api.cursor.ClickType;
import io.github.astrarre.gui.v1.api.cursor.Cursor;
import io.github.astrarre.gui.v1.api.cursor.CursorType;
import io.github.astrarre.gui.v1.api.cursor.MouseListener;
import io.github.astrarre.gui.v1.api.util.GuiRenderable;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.Nullable;

/**
 * @see Standard
 */
public abstract class AButton extends AComponent implements MouseListener {
	Runnable callback;
	GuiRenderable tooltip;
	boolean pressed;

	/**
	 * @param callback the listener len call when the button is pressed
	 */
	public AButton(@Nullable Runnable callback) {
		this.callback = callback;
	}

	/**
	 * @see #callback(Runnable)
	 */
	public static Standard icon(Icon pressed, Icon hover, Icon default_) {
		return new Standard(pressed, hover, default_, null);
	}

	/**
	 * @see #callback(Runnable)
	 */
	public static Standard icon(Icon.Group group) {
		return new Standard(group.pressed(), group.hover(), group.normal(), null);
	}

	/**
	 * @param callback the listener len call when the button is pressed
	 */
	public AButton callback(@Nullable Runnable callback) {
		this.callback = callback;
		return this;
	}

	public AButton tooltip(@Nullable GuiRenderable renderable) {
		this.tooltip = renderable;
		return this;
	}

	@Override
	public boolean mouseClicked(Cursor cursor, ClickType type) {
		this.pressed = true;
		return true;
	}

	@Override
	public boolean mouseReleased(Cursor cursor, ClickType type) {
		if(this.pressed) {
			this.onClick();
		}
		return true;
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		this.pressed &= cursor.isPressed(ClickType.Standard.LEFT);
		if(this.pressed) {
			this.renderPressed(cursor, render);
		} else if(this.isIn(cursor)) {
			this.renderHighlighted(cursor, render);
			if(this.tooltip != null) {
				this.tooltip.render(cursor, render);
			}
		} else {
			this.renderDefault(cursor, render);
		}
	}

	@Override
	protected void onMouseEnter(Cursor cursor, Render3d render) {
		cursor.setType(CursorType.Standard.HAND);
	}

	@Override
	protected void onMouseExit(Cursor cursor, Render3d render) {
		cursor.setType(CursorType.Standard.ARROW);
	}

	protected abstract void renderPressed(Cursor cursor, Render3d render);

	protected abstract void renderHighlighted(Cursor cursor, Render3d render);

	protected abstract void renderDefault(Cursor cursor, Render3d render);

	protected void onClick() {
		if(this.callback != null) {
			this.callback.run();
		}
	}

	public static class Standard extends AButton implements FocusableComponent {
		Icon press, hover, state;

		/**
		 * {@inheritDoc}
		 */
		public Standard(Icon pressed, Icon hover, Icon default_, @Nullable Runnable callback) {
			super(callback);
			this.press = pressed;
			this.hover = hover;
			this.state = default_;
			this.validate();
			this.setBounds(pressed.width(), pressed.height());
			this.lockBounds();
		}

		public Icon pressed() {
			return this.press;
		}

		public Standard setPressed(Icon pressed) {
			this.press = pressed;
			this.validate();
			return this;
		}

		public Icon highlighted() {
			return this.hover;
		}

		public Standard setHover(Icon hover) {
			this.hover = hover;
			this.validate();
			return this;
		}

		public Icon default_() {
			return this.state;
		}

		public Standard setDefault(Icon default_) {
			this.state = default_;
			this.validate();
			return this;
		}

		protected void validate() {
			Validate.equals((e, v) -> "Icons must be same dims " + e + " vs " + v, this.press.height(), this.hover.height(), this.state.height());
			Validate.equals((e, v) -> "Icons must be same dims " + e + " vs " + v, this.press.width(), this.hover.width(), this.state.width());
		}

		protected void renderFocusedBackground(Cursor cursor, Render3d render) {
			try(var ignore = render.translate(-5, -5)) {
				render.fill().rect(0xFF3333FF, 0, 0, this.press.width() + 10, this.press.height() + 10);
			}
		}

		@Override
		protected void renderPressed(Cursor cursor, Render3d render) {
			if(this.isFocused()) {
				this.renderFocusedBackground(cursor, render);
			}
			this.press.render(render);
		}

		@Override
		protected void renderHighlighted(Cursor cursor, Render3d render) {
			if(this.isFocused()) {
				this.renderFocusedBackground(cursor, render);
			}
			this.hover.render(render);
		}

		@Override
		protected void renderDefault(Cursor cursor, Render3d render) {
			if(this.isFocused()) {
				this.renderFocusedBackground(cursor, render);
			}
			this.state.render(render);
		}
	}
}

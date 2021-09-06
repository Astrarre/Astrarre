package io.github.astrarre.gui.v1.api.component;

import java.util.function.Consumer;

import io.github.astrarre.gui.v1.api.listener.cursor.ClickType;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.listener.cursor.CursorType;
import io.github.astrarre.gui.v1.api.listener.cursor.MouseListener;
import io.github.astrarre.gui.v1.api.util.GuiRenderable;
import io.github.astrarre.rendering.v1.api.plane.TooltipBuilder;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.util.v0.api.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see Standard
 */
public abstract class AButton extends AHoverableComponent implements MouseListener, ToggleableComponent {
	Runnable callback;
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
	public static Standard icon(Icon pressed, Icon hover, Icon default_, @Nullable Icon disabled) {
		return new Standard(pressed, hover, default_, disabled, null);
	}

	/**
	 * @see #callback(Runnable)
	 */
	public static Standard icon(Icon.Group group) {
		return new Standard(group.pressed(), group.hover(), group.normal(), group.disabled(), null);
	}

	/**
	 * @param callback the listener len call when the button is pressed
	 */
	public AButton callback(@Nullable Runnable callback) {
		this.callback = callback;
		return this;
	}

	public AButton tooltipDirect(@NotNull Consumer<TooltipBuilder> consumer) {
		this.onHover.andThen((cursor, render) -> {
			TooltipBuilder builder = render.tooltip();
			consumer.accept(builder);
			builder.text();
		});
		return this;
	}

	@Override
	public boolean mouseClicked(Cursor cursor, ClickType type) {
		return (this.pressed = this.isEnabled());
	}

	@Override
	public boolean mouseReleased(Cursor cursor, ClickType type) {
		if(this.pressed) {
			this.onClick();
		}
		return this.pressed;
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		this.pressed &= cursor.isPressed(ClickType.Standard.LEFT);
		if(!this.isEnabled()) {
			this.renderDisabled(cursor, render);
		} else if(this.pressed) {
			this.renderPressed(cursor, render);
		} else if(this.isIn(cursor)) {
			this.renderHighlighted(cursor, render);
		} else {
			this.renderDefault(cursor, render);
		}
	}

	@Override
	protected void onMouseEnter(Cursor cursor, Render3d render) {
		if(this.isEnabled()) {
			cursor.setType(CursorType.Standard.HAND);
		}
	}

	@Override
	protected void onMouseExit(Cursor cursor, Render3d render) {
		if(this.isEnabled()) {
			cursor.setType(CursorType.Standard.ARROW);
		}
	}

	protected abstract void renderPressed(Cursor cursor, Render3d render);

	protected abstract void renderHighlighted(Cursor cursor, Render3d render);

	protected abstract void renderDefault(Cursor cursor, Render3d render);

	protected abstract void renderDisabled(Cursor cursor, Render3d render);

	protected void onClick() {
		if(this.callback != null) {
			this.callback.run();
		}
	}

	public static class Standard extends AButton implements FocusableComponent {
		Icon press, hover, state, disabled;

		/**
		 * {@inheritDoc}
		 */
		public Standard(Icon pressed, Icon hover, Icon default_, @Nullable Icon disabled, @Nullable Runnable callback) {
			super(callback);
			this.press = pressed;
			this.hover = hover;
			this.state = default_;
			this.disabled = disabled;
			this.validate();
			this.setBounds(pressed.width(), pressed.height());
			this.lockBounds(true);
		}

		public Icon getDisabled() {
			return this.disabled;
		}

		public Standard setDisabled(Icon disabled) {
			this.disabled = disabled;
			return this;
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
			render.fill().rect(0xFF3333FF, -2, -2, this.press.width() + 4, this.press.height() + 4);
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

		@Override
		protected void renderDisabled(Cursor cursor, Render3d render) {
			if(this.disabled != null) {
				this.disabled.render(render);
			}
		}
	}
}

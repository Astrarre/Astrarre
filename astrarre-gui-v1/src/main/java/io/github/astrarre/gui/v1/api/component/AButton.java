package io.github.astrarre.gui.v1.api.component;

import io.github.astrarre.gui.v1.api.AComponent;
import io.github.astrarre.gui.v1.api.cursor.ClickType;
import io.github.astrarre.gui.v1.api.cursor.Cursor;
import io.github.astrarre.gui.v1.api.cursor.MouseListener;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import org.jetbrains.annotations.Nullable;

/**
 * @see Icon
 */
public abstract class AButton extends AComponent implements MouseListener {
	Runnable callback;
	boolean pressed;

	/**
	 * @param callback the listener to call when the button is pressed
	 */
	public AButton(@Nullable Runnable callback) {
		this.callback = callback;
	}

	/**
	 * @param callback the listener to call when the button is pressed
	 */
	public void setCallback(@Nullable Runnable callback) {
		this.callback = callback;
	}

	@Override
	public boolean onMouseClicked(Cursor cursor, ClickType type) {
		this.pressed = true;
		return true;
	}

	@Override
	public boolean onMouseReleased(Cursor cursor, ClickType type) {
		if(this.pressed) {
			this.onClick();
		}
		return true;
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		this.pressed &= cursor.isPressed();
		if(this.pressed) {
			this.renderPressed(cursor, render);
		} else if(this.isIn(cursor)) {
			this.renderHighlighted(cursor, render);
		} else {
			this.renderDefault(cursor, render);
		}
	}

	protected abstract void renderPressed(Cursor cursor, Render3d render);

	protected abstract void renderHighlighted(Cursor cursor, Render3d render);

	protected abstract void renderDefault(Cursor cursor, Render3d render);

	protected void onClick() {
		if(this.callback != null) {
			this.callback.run();
		}
	}

	public static class Icon extends AButton {
		final io.github.astrarre.gui.v1.api.component.icon.Icon pressed, highlighted, default_;

		/**
		 * {@inheritDoc}
		 */
		public Icon(io.github.astrarre.gui.v1.api.component.icon.Icon pressed,
				io.github.astrarre.gui.v1.api.component.icon.Icon highlighted,
				io.github.astrarre.gui.v1.api.component.icon.Icon default_,
				@Nullable Runnable callback) {
			super(callback);
			this.pressed = pressed;
			this.highlighted = highlighted;
			this.default_ = default_;
		}

		@Override
		protected void renderPressed(Cursor cursor, Render3d render) {
			this.pressed.render(render);
		}

		@Override
		protected void renderHighlighted(Cursor cursor, Render3d render) {
			this.highlighted.render(render);
		}

		@Override
		protected void renderDefault(Cursor cursor, Render3d render) {
			this.default_.render(render);
		}
	}
}

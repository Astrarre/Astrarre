package io.github.astrarre.rendering.v1.api.plane.icon.backgrounds;

import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;

/**
 * Renders a square like the ones found on item slots in inventory GUIs
 */
public record SlotBackgroundIcon(float width, float height, State state) implements Icon {
	public SlotBackgroundIcon(float width, float height) {
		this(width, height, State.DEFAULT);
	}

	@Override
	public void render(Render3d render) {
		// background
		render.fill().rect(this.state.background, 0, 0, this.width, this.height);
		// top shade
		render.fill().rect(this.state.topLeft, 0, 0, this.width - 1, 1);
		render.fill().rect(this.state.topLeft, 0, 0, 1, this.height - 1);
		// bottom shade
		render.fill().rect(this.state.bottomRight, this.width - 1, 1, 1, this.height - 1);
		render.fill().rect(this.state.bottomRight, 1, this.height - 1, this.width - 1, 1);
	}

	public enum State {
		INVERTED(0xff8b8b8b, 0xffffffff, 0xff373737),
		DEFAULT(0xff8b8b8b, 0xff373737, 0xffffffff),
		DISABLED(0xff373737, 0xff494949, 0xff2d2d2d),

		/**
		 * The button in the beacon inventory uses the exact same colors/texture as regular slots, except it's highlight texture is different
		 */
		HIGHLIGHTED_BUTTON(0xff7778a0, 0xffcfd0f7, 0xff373860);

		final int background, topLeft, bottomRight;

		State(int background, int topLeft, int bottomRight) {
			this.background = background;
			this.topLeft = topLeft;
			this.bottomRight = bottomRight;
		}
	}
}

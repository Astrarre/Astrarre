package io.github.astrarre.rendering.v1.api.plane.icon;

import io.github.astrarre.rendering.v1.api.plane.Texture;
import io.github.astrarre.rendering.v1.api.plane.icon.backgrounds.SlotBackgroundIcon;
import io.github.astrarre.util.v0.api.Id;

/**
 * only works with fapi so mod resources can load
 */
public class Icons {
	static final Id ICON_ATLAS = Id.create("astrarre", "textures/gui/icons.png");

	/**
	 * a 7x7 icon of a shaded blue square with the lowercase letter 'i' on it
	 */
	public static final Icon INFO = icon(0, 0, 7, 7);
	/**
	 * a 7x7 icon of a shaded semi state dark blue square with the lowercase letter 'i' on it
	 */
	public static final Icon INFO_PRESSED = icon(0, 7, 7, 7);
	/**
	 * a 7x7 icon of a shaded red square with the '!' character on it
	 */
	public static final Icon ALERT = icon(7, 0, 7, 7);
	/**
	 * a 7x7 icon of a shaded semi state dark red square with the '!' character on it
	 */
	public static final Icon ALERT_PRESSED = icon(7, 7, 7, 7);

	/**
	 * a 8x8 icon of a shaded bright green square with a checkmark on it
	 */
	public static final Icon CHECK = icon(14, 0, 8, 8);

	/**
	 * a 8x8 icon of a shaded semi state dark green square with a checkmark on it
	 */
	public static final Icon CHECK_PRESSED = icon(14, 8, 8, 8);

	/**
	 * a 8x8 icon of a shaded red square with an 'offX' in the middle
	 */
	public static final Icon X = icon(22, 0, 8, 8);

	/**
	 * a 8x8 icon of a shaded semi state red square with an 'offX' in the middle
	 */
	public static final Icon X_PRESSED = icon(22, 8, 8, 8);

	/**
	 * the 'off' flame furnace texture (14x14)
	 */
	public static final Icon FURNACE_FLAME_OFF = icon(30, 14, 14, 14);
	/**
	 * the 'on' flame furnace texture (14x14)
	 */
	public static final Icon FURNACE_FLAME_ON = icon(30, 28, 14, 14);
	/**
	 * the background texture of the progress bar in the furnace (transparent background)
	 */
	public static final Icon FURNACE_PROGRESS_BAR_EMPTY = icon(58, 0, 22, 16);
	/**
	 * the 'filled' progress bar texture in the furnace (transparent background)
	 */
	public static final Icon FURNACE_PROGRESS_BAR_FULL = icon(58, 16, 22, 16);

	// -- medium button --
	// the active, highlighted, disabled and pressed textures of a 20x20 button
	public static final Icon MEDIUM_BUTTON_ACTIVE = icon(0, 16, 20, 20);
	public static final Icon MEDIUM_BUTTON_HIGHLIGHTED = icon(0, 36, 20, 20);
	public static final Icon MEDIUM_BUTTON_DISABLED = icon(0, 56, 20, 20);
	public static final Icon MEDIUM_BUTTON_PRESSED = icon(0, 76, 20, 20);

	// arrow buttons, 7x7 textures with pressed and unpressed variants
	public static final Icon UP_ARROW = icon(30, 0, 7, 7);
	public static final Icon DOWN_ARROW = icon(37, 0, 7, 7);
	public static final Icon UP_ARROW_PRESSED = icon(30, 7, 7, 7);
	public static final Icon DOWN_ARROW_PRESSED = icon(37, 7, 7, 7);
	public static final Icon RIGHT_ARROW = icon(44, 0, 7, 7);
	public static final Icon LEFT_ARROW = icon(51, 0, 7, 7);
	public static final Icon RIGHT_ARROW_PRESSED = icon(44, 7, 7, 7);
	public static final Icon LEFT_ARROW_PRESSED = icon(51, 7, 7, 7);

	public static class Groups {
		public static final Icon.Group INFO = Icon.group(Icons.INFO, Icons.INFO.highlighted(), INFO_PRESSED, INFO_PRESSED.darkened());
		public static final Icon.Group ALERT = Icon.group(Icons.ALERT, Icons.ALERT.highlighted(), ALERT_PRESSED, ALERT_PRESSED.darkened());
		public static final Icon.Group CHECK = Icon.group(Icons.CHECK, Icons.CHECK.highlighted(), CHECK_PRESSED, CHECK_PRESSED.darkened());
		public static final Icon.Group X = Icon.group(Icons.X, Icons.X.highlighted(), X_PRESSED, X_PRESSED.darkened());
		public static final Icon.Group UP_ARROW = Icon.group(Icons.UP_ARROW, Icons.UP_ARROW.highlighted(), UP_ARROW_PRESSED, UP_ARROW_PRESSED.darkened());
		public static final Icon.Group DOWN_ARROW = Icon.group(Icons.DOWN_ARROW, Icons.DOWN_ARROW.highlighted(), DOWN_ARROW_PRESSED, DOWN_ARROW_PRESSED.darkened());
		public static final Icon.Group RIGHT_ARROW = Icon.group(Icons.RIGHT_ARROW, Icons.RIGHT_ARROW.highlighted(), RIGHT_ARROW_PRESSED, RIGHT_ARROW_PRESSED.darkened());
		public static final Icon.Group LEFT_ARROW = Icon.group(Icons.LEFT_ARROW, Icons.LEFT_ARROW.highlighted(), LEFT_ARROW_PRESSED, LEFT_ARROW_PRESSED.darkened());

		/**
		 * A button inspired by the button in the beacon gui but of arbitrary size
		 */
		public static Icon.Group button(float width, float height) {
			Icon def = Icon.slot(width, height, SlotBackgroundIcon.State.INVERTED);
			Icon pressed = Icon.slot(width, height, SlotBackgroundIcon.State.DEFAULT);
			Icon highlighted = Icon.slot(width, height, SlotBackgroundIcon.State.HIGHLIGHTED_BUTTON);
			Icon disabled = Icon.slot(width, height, SlotBackgroundIcon.State.DISABLED);
			return Icon.group(def, highlighted, pressed, disabled);
		}
	}

	private static Icon icon(int x1, int y1, int width, int height) {
		return Icon.tex(Texture.create(ICON_ATLAS, 256, 256, x1, y1, width, height), width, height);
	}
}
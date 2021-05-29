package io.github.astrarre.gui.v0.api;


import io.github.astrarre.rendering.v0.api.textures.Sprite;
import io.github.astrarre.util.v0.api.Id;

/**
 * only works with fapi so mod resources can load
 */
public class AstrarreIcons {
	private static final Sprite ICON = Sprite.of(Id.create("astrarre-gui-v0", "textures/gui/icons.png"));

	/**
	 * a 7x7 icon of a shaded blue square with the lowercase letter 'i' on it
	 */
	public static final Sprite.Sized INFO = icon(0, 0, 7, 7);
	/**
	 * a 7x7 icon of a shaded dark blue square with the lowercase letter 'i' on it
	 */
	public static final Sprite.Sized INFO_DARK = icon(0, 7, 7, 7);
	/**
	 * a 7x7 icon of a shaded red square with the '!' character on it
	 */
	public static final Sprite.Sized ALERT = icon(7, 0, 7, 7);
	/**
	 * a 7x7 icon of a shaded dark red square with the '!' character on it
	 */
	public static final Sprite.Sized ALERT_DARK = icon(7, 7, 7, 7);

	/**
	 * a 8x8 icon of a shaded bright green square with a checkmark on it
	 */
	public static final Sprite.Sized CHECK = icon(14, 0, 8, 8);

	/**
	 * a 8x8 icon of a shaded red square with an 'x' in the middle
	 */
	public static final Sprite.Sized X = icon(23, 0, 8, 8);

	/**
	 * the 'off' flame furnace texture (14x14)
	 */
	public static final Sprite.Sized FURNACE_FLAME_OFF = icon(30, 14, 14, 14);
	/**
	 * the 'on' flame furnace texture (14x14)
	 */
	public static final Sprite.Sized FURNACE_FLAME_ON = icon(30, 28, 14, 14);
	/**
	 * the background texture of the progress bar in the furnace (transparent background)
	 */
	public static final Sprite.Sized FURNACE_PROGRESS_BAR_EMPTY = icon(58, 0, 22, 16);
	/**
	 * the 'filled' progress bar texture in the furnace (transparent background)
	 */
	public static final Sprite.Sized FURNACE_PROGRESS_BAR_FULL = icon(58, 16, 22, 16);

	// -- medium button --
	// the active, highlighted, disabled and pressed textures of a 20x20 button
	public static final Sprite.Sized MEDIUM_BUTTON_ACTIVE = icon(0, 14, 20, 20);
	public static final Sprite.Sized MEDIUM_BUTTON_HIGHLIGHTED = icon(0, 34, 20, 20);
	public static final Sprite.Sized MEDIUM_BUTTON_DISABLED = icon(0, 54, 20, 20);
	public static final Sprite.Sized MEDIUM_BUTTON_PRESSED = icon(0, 74, 20, 20);

	// arrow buttons, 7x7 textures with pressed and unpressed variants
	public static final Sprite.Sized UP_ARROW_ACTIVE = icon(30, 0, 7, 7);
	public static final Sprite.Sized DOWN_ARROW_ACTIVE = icon(37, 0, 7, 7);
	public static final Sprite.Sized UP_ARROW_PRESSED = icon(30, 7, 7, 7);
	public static final Sprite.Sized DOWN_ARROW_PRESSED = icon(37, 7, 7, 7);
	public static final Sprite.Sized RIGHT_ARROW_ACTIVE = icon(44, 0, 7, 7);
	public static final Sprite.Sized LEFT_ARROW_ACTIVE = icon(51, 0, 7, 7);
	public static final Sprite.Sized RIGHT_ARROW_PRESSED = icon(44, 7, 7, 7);
	public static final Sprite.Sized LEFT_ARROW_PRESSED = icon(51, 7, 7, 7);

	private static Sprite.Sized icon(int x1, int y1, int width, int height) {
		return ICON.cutout(x1 / 256f, y1 / 256f, width / 256f, height / 256f).sized(width, height);
	}
}

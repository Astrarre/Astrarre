package io.github.astrarre.gui.v0.api;

import io.github.astrarre.rendering.v0.api.textures.Texture;
import io.github.astrarre.rendering.v0.api.textures.TexturePart;

public class AstrarreIcons {
	private static final Texture ICON = new Texture("astrarre-gui-v0", "textures/gui/icons.png", 256, 256);

	/**
	 * a 7x7 icon of a shaded blue square with the lowercase letter 'i' on it
	 */
	public static final TexturePart INFO = icon(0, 0, 7, 7);
	/**
	 * a 7x7 icon of a shaded dark blue square with the lowercase letter 'i' on it
	 */
	public static final TexturePart INFO_DARK = icon(0, 7, 7, 7);
	/**
	 * a 7x7 icon of a shaded red square with the '!' character on it
	 */
	public static final TexturePart ALERT = icon(7, 0, 7, 7);
	/**
	 * a 7x7 icon of a shaded dark red square with the '!' character on it
	 */
	public static final TexturePart ALERT_DARK = icon(7, 7, 7, 7);

	/**
	 * a 9x9 icon of a shaded bright green square with a checkmark on it
	 */
	public static final TexturePart CHECK = icon(14, 0, 9, 9);

	/**
	 * a 9x9 icon of a shaded red square with an 'x' in the middle
	 */
	public static final TexturePart X = icon(23, 0, 9, 9);

	// -- medium button --
	// the active, highlighted, disabled and pressed textures of a 20x20 button
	public static final TexturePart MEDIUM_BUTTON_ACTIVE = icon(0, 14, 20, 20);
	public static final TexturePart MEDIUM_BUTTON_HIGHLIGHTED = icon(0, 34, 20, 20);
	public static final TexturePart MEDIUM_BUTTON_DISABLED = icon(0, 54, 20, 20);
	public static final TexturePart MEDIUM_BUTTON_PRESSED = icon(0, 74, 20, 20);

	// arrow buttons, 7x7 textures with pressed and unpressed variants
	public static final TexturePart UP_ARROW_ACTIVE = icon(0, 32, 7, 7);
	public static final TexturePart DOWN_ARROW_ACTIVE = icon(0, 39, 7, 7);
	public static final TexturePart UP_ARROW_PRESSED = icon(7, 32, 7, 7);
	public static final TexturePart DOWN_ARROW_PRESSED = icon(7, 39, 7, 7);
	public static final TexturePart RIGHT_ARROW_ACTIVE = icon(0, 46, 7, 7);
	public static final TexturePart LEFT_ARROW_ACTIVE = icon(0, 53, 7, 7);
	public static final TexturePart RIGHT_ARROW_PRESSED = icon(7, 46, 7, 7);
	public static final TexturePart LEFT_ARROW_PRESSED = icon(7, 53, 7, 7);

	private static TexturePart icon(int x1, int y1, int width, int height) {
		return new TexturePart(ICON, x1, y1, width, height);
	}
}

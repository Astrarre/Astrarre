package io.github.astrarre.gui.v0.api;

public class GUIs {
	public static final int MIN_WIDTH = 320;
	/**
	 * this is the minimum 'guaranteed' window in which you can render.
	 * In auto gui mode (in video settings) will rescale the coordinate grid to ensure that this 'window' in the center of the screen is always visible.
	 * For normal GUIs (centered guis, like inventories for example): it's recommended to use this scale.
	 */
	public static final int MIN_HEIGHT = 240;
}

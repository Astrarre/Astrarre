package io.github.astrarre.gui.v0.api.components;

import io.github.astrarre.gui.v0.api.Graphics2d;
import io.github.astrarre.gui.v0.api.cursor.Cursor;
import org.jetbrains.annotations.Nullable;

/**
 * a component in a gui
 */
public abstract class Widget extends Component {
	// todo Cursor class for cursor Items/Files/Whatever (custom Component for custom cursor stacks)

	/**
	 * @param g2d automatically offset by {@link #getLocation(float, float)}
	 */
	@Override
	public abstract void render(Graphics2d g2d, float tickDelta);

	/**
	 * like all other methods, the coordinates are relative to {@link #getLocation(float, float)}
	 * this is fired every time the mouse moves and is found to be inside the widget
	 * @return true if the mouse over event was handled, and should not be passed to other components
	 */
	public boolean onMouseOver(float x, float y) {return false;}

	/**
	 * fired when the mouse leaves the bounds of the widget
	 */
	public void onMouseLeave() {}

	/**
	 * called when the component is clicked. This method does not differentiate between the user holding the button down and clicking it briefly.
	 *
	 * @param button the key code for the button
	 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
	 */
	public boolean onPress(Cursor cursor, int button) {return false;}
	public boolean onRelease(Cursor cursor, int button) {return false;}
	public boolean mouseScrolled(float x, float y, float amount) {return false;}
	public boolean mouseDragged(Cursor cursor, int button, float deltaX, float deltaY) {
		return false;
	}

	/**
	 * This method does not differentiate between the user holding the button down and clicking it briefly. It also doesn't check if the Widget is
	 * focused or not. (neither do any of the other methods)
	 * This method is fired globally!
	 * @param keyCode the named key code of the event as described in the {@link org.lwjgl.glfw.GLFW GLFW} class
	 * @param scanCode the unique/platform-specific scan code of the keyboard input
	 * @param modifiers a GLFW bitfield describing the modifier keys that are held down (see {@linkplain
	 *        https://www.glfw.org/docs/3.3/group__mods.html GLFW Modifier key flags})
	 */
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	/**
	 * This method is fired globally!
	 * @see #keyPressed(int, int, int)
	 */
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	/**
	 * This method is fired only when the widget is focused
	 * Invoked when the user types something
	 *
	 * @param chr the captured character
	 * @param keyCode the associated key code
	 */
	public boolean charTyped(char chr, int keyCode) {return false;}

	/**
	 * called when the user tries to select the 'next' or 'previous' element (think tab key) if for example this widget is a list, you would return
	 * the next element in the list, or null if you've reached the end. It is expected that the returned widget has already has it's {@link
	 * #onFocused()} method called and was properly handled
	 *
	 * @param forwards true if the user is trying to select the next element, false if previous
	 * @return the 'next'/'previous' element, or null if this widget has no valid children
	 */
	@Nullable
	public Widget focused(boolean forwards) {return null;}

	/**
	 * called when the widget is focused on. If the widget can be focused, return true. (eg. text boxes, buttons)
	 *
	 * @return false if the widget cannot be focused on
	 */
	public boolean onFocused() {return false;}
}

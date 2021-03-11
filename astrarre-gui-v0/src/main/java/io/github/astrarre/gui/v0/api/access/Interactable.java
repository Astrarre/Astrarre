package io.github.astrarre.gui.v0.api.access;

import io.github.astrarre.gui.v0.api.RootContainer;

import net.minecraft.client.gui.Element;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * implement on Drawable
 * @see Element
 */
public interface Interactable {
	@Environment(EnvType.CLIENT)
	default void mouseMoved(RootContainer container, double mouseX, double mouseY) {
	}

	/**
	 * Callback for when a mouse button down event
	 * has been captured.
	 *
	 * The button number is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 *
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Mouse#onMouseButton(long, int, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
	 * @param mouseX the X coordinate of the mouse
	 * @param mouseY the Y coordinate of the mouse
	 * @param button the mouse button number
	 */
	@Environment(EnvType.CLIENT)
	default boolean mouseClicked(RootContainer container, double mouseX, double mouseY, int button) {
		return false;
	}

	/**
	 * Callback for when a mouse button release event
	 * has been captured.
	 *
	 * The button number is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 *
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Mouse#onMouseButton(long, int, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
	 * @param mouseX the X coordinate of the mouse
	 * @param mouseY the Y coordinate of the mouse
	 * @param button the mouse button number
	 */
	@Environment(EnvType.CLIENT)
	default boolean mouseReleased(RootContainer container, double mouseX, double mouseY, int button) {
		return false;
	}

	/**
	 * Callback for when a mouse button drag event
	 * has been captured.
	 *
	 * The button number is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 *
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Mouse#onCursorPos(long, double, double)
	 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
	 * @param mouseX the current X coordinate of the mouse
	 * @param mouseY the current Y coordinate of the mouse
	 * @param button the mouse button number
	 * @param deltaX the difference of the current X with the previous X coordinate
	 * @param deltaY the difference of the current Y with the previous Y coordinate
	 */
	@Environment(EnvType.CLIENT)
	default boolean mouseDragged(RootContainer container, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return false;
	}

	/**
	 * Callback for when a mouse button scroll event
	 * has been captured.
	 *
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Mouse#onMouseScroll(long, double, double)
	 * @param mouseX the X coordinate of the mouse
	 * @param mouseY the Y coordinate of the mouse
	 * @param amount value is {@code > 1} if scrolled down, {@code < 1} if scrolled up
	 */
	@Environment(EnvType.CLIENT)
	default boolean mouseScrolled(RootContainer container, double mouseX, double mouseY, double amount) {
		return false;
	}

	/**
	 * Callback for when a key down event has been captured.
	 *
	 * The key code is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 *
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Keyboard#onKey(long, int, int, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_KEY_Q
	 * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
	 * @param keyCode the named key code of the event as described in the {@link org.lwjgl.glfw.GLFW GLFW} class
	 * @param scanCode the unique/platform-specific scan code of the keyboard input
	 * @param modifiers a GLFW bitfield describing the modifier keys that are held down (see {@linkplain https://www.glfw.org/docs/3.3/group__mods.html GLFW Modifier key flags})
	 */
	@Environment(EnvType.CLIENT)
	default boolean keyPressed(RootContainer container, int keyCode, int scanCode, int modifiers) {
		return false;
	}

	/**
	 * Callback for when a key down event has been captured.
	 *
	 * The key code is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 *
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Keyboard#onKey(long, int, int, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_KEY_Q
	 * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
	 * @param keyCode the named key code of the event as described in the {@link org.lwjgl.glfw.GLFW GLFW} class
	 * @param scanCode the unique/platform-specific scan code of the keyboard input
	 * @param modifiers a GLFW bitfield describing the modifier keys that are held down (see {@linkplain https://www.glfw.org/docs/3.3/group__mods.html GLFW Modifier key flags})
	 */
	@Environment(EnvType.CLIENT)
	default boolean keyReleased(RootContainer container, int keyCode, int scanCode, int modifiers) {
		return false;
	}

	/**
	 * Callback for when a character input has been captured.
	 *
	 * The key code is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 *
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Keyboard#onChar(long, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_KEY_Q
	 * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
	 * @param chr the captured character
	 * @param modifiers a GLFW bitfield describing the modifier keys that are held down (see <a href="https://www.glfw.org/docs/3.3/group__mods.html">GLFW Modifier key flags</a>)
	 */
	@Environment(EnvType.CLIENT)
	default boolean charTyped(RootContainer container, char chr, int modifiers) {
		return false;
	}

	/**
	 * This method is for handling tab / ctrl+tab calls, if the function returns false
	 * @return true if the component handled the cycle forward call.
	 */
	@Environment(EnvType.CLIENT)
	default boolean handleFocusCycle(RootContainer container, boolean forward) {
		return false;
	}

	default boolean canFocus(RootContainer container) {return false;}

	@Environment(EnvType.CLIENT)
	default void onFocus(RootContainer container) {}
	@Environment(EnvType.CLIENT)
	default void onLostFocus(RootContainer container) {}

	/**
	 * @return {@code true} if the mouse hover event should be captured (Panel already checks if it's inside the Drawable bounds)
	 * @param mouseX the X coordinate of the mouse
	 * @param mouseY the Y coordinate of the mouse
	 * @see #mouseHover(RootContainer, double, double)
	 */
	default boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	default void mouseHover(RootContainer container, double mouseX, double mouseY) {
	}

	/**
	 * fired when the mouse moves out of the bounds of the component. This is only fired if the mouseHover event is handled
	 */
	@Environment(EnvType.CLIENT)
	default void onLoseHover(RootContainer container) {}
}

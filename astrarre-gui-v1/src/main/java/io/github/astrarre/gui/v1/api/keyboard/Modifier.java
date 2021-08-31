package io.github.astrarre.gui.v1.api.keyboard;

import io.github.astrarre.gui.internal.util.Flag;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

/**
 * Key modifiers, eg. shift, control, alt, etc.
 * Can use reference equality
 */
public interface Modifier extends Flag {

	@ApiStatus.Internal
	int glfwFlag();

	@Override
	default int flag() {
		return this.glfwFlag();
	}

	/**
	 * Some of the names, such as command or windows are approximations.
	 * I feel these names are more popular/recognizable than the glfw name.
	 */
	enum Standard implements Modifier {
		SHIFT(GLFW.GLFW_MOD_SHIFT),
		CONTROL(GLFW.GLFW_MOD_CONTROL),
		ALT(GLFW.GLFW_MOD_ALT),
		COMMAND(GLFW.GLFW_MOD_SUPER),
		WINDOWS(COMMAND),
		CAPSLOCK(GLFW.GLFW_MOD_CAPS_LOCK),
		NUMLOCK(GLFW.GLFW_MOD_NUM_LOCK)
		;

		final int glfwFlag;

		Standard(Standard psudo) {
			this(psudo.glfwFlag);
		}

		Standard(int flag) {
			this.glfwFlag = flag;
		}

		@Override
		public int glfwFlag() {
			return this.glfwFlag;
		}
	}
}

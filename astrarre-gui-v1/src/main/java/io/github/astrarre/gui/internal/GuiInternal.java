package io.github.astrarre.gui.internal;

import java.util.Arrays;
import java.util.Collection;

import io.github.astrarre.gui.internal.util.IntFlags;
import io.github.astrarre.gui.v1.api.cursor.ClickType;
import io.github.astrarre.gui.v1.api.keyboard.Key;
import io.github.astrarre.gui.v1.api.keyboard.Modifier;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.math.MathHelper;

public class GuiInternal {
	static final Modifier[] MODIFIERS = new Modifier[32];
	static final Logger LOGGER = LogManager.getLogger(GuiInternal.class);
	static ClickType[] clickTypes = ClickType.Standard.values();
	static final ClickType UNKNOWN = () -> -1;
	static final Int2ObjectMap<Key> GLFW_TO_KEY = new Int2ObjectOpenHashMap<>();
	static {
		GLFW_TO_KEY.put(-1, () -> -1);
		for(Key.Standard value : Key.Standard.values()) {
			GLFW_TO_KEY.put(value.glfwId(), value);
		}

		for(Modifier.Standard standard : Modifier.Standard.values()) {
			MODIFIERS[MathHelper.log2(standard.glfwFlag())] = standard;
		}
		for(int i = 0; i < MODIFIERS.length; i++) {
			Modifier modifier = MODIFIERS[i];
			if(modifier == null) {
				int flag = 1 << i;
				MODIFIERS[i] = () -> flag;
			}
		}
	}

	public static Collection<Modifier> of(int modifiers) {
		return new IntFlags<>(MODIFIERS, modifiers, false);
	}

	public static ClickType clickTypeByGlfwId(int glfwId) {
		ClickType type;
		if(glfwId < 0) {
			return UNKNOWN;
		} else if(glfwId < clickTypes.length && (type = clickTypes[glfwId]) != null) {
			return type;
		} else {
			LOGGER.warn("Found non-standard mouse key id " + glfwId);
			clickTypes = Arrays.copyOf(clickTypes, glfwId+1);
			return clickTypes[glfwId] = () -> glfwId;
		}
	}

	public static Key keyByGlfwId(int glfwId) {
		return GLFW_TO_KEY.computeIfAbsent(glfwId, i -> () -> i);
	}
}

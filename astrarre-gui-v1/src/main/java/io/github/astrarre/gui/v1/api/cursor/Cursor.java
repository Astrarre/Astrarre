package io.github.astrarre.gui.v1.api.cursor;

import java.nio.file.Path;
import java.util.List;
import java.util.function.LongSupplier;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.util.v0.api.SafeCloseable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;

public interface Cursor extends SafeCloseable {
	Key<List<Path>> FILES = (render, cursor, data) -> {}; // todo impl

	List<Path> files();

	float x();

	float y();

	Type getType();

	void setType(Type type);

	<T> void set(Key<T> key, T value);

	<T> T get(Key<T> key);

	boolean isPressed();

	Cursor transformed(Transform2d transform);

	interface Key<T> {
		void render(Render3d render, Cursor cursor, T data);

		@Nullable
		@ApiStatus.OverrideOnly
		default T getState() {
			return null;
		}

		@ApiStatus.OverrideOnly
		default boolean setState(T val) {
			return false;
		}
	}

	enum Type {
		NORMAL(GLFW.GLFW_CURSOR_NORMAL),
		HIDDEN(GLFW.GLFW_CURSOR_HIDDEN),
		DISABLED(GLFW.GLFW_CURSOR_DISABLED),
		ARROW(GLFW.GLFW_ARROW_CURSOR),
		/**
		 * the cursor for text boxes basically
		 */
		IBEAM(GLFW.GLFW_IBEAM_CURSOR),
		CROSSHAIR(GLFW.GLFW_CROSSHAIR_CURSOR),
		HAND(GLFW.GLFW_HAND_CURSOR),
		/**
		 * horizontal resize (like in ms paint)
		 */
		HRESIZE(GLFW.GLFW_HRESIZE_CURSOR),
		/**
		 * vertical resize (like in ms paint)
		 */
		VRESIZE(GLFW.GLFW_VRESIZE_CURSOR);
		
		final LongSupplier typeId;

		Type(int typeId) {
			this.typeId = new LongSupplier() {
				long memoized;
				@Override
				public long getAsLong() {
					long memo = this.memoized;
					if(memo == 0) {
						this.memoized = memo = GLFW.glfwCreateStandardCursor(typeId);
					}
					return memo;
				}
			};
		}

		public void bind() {
			if (!RenderSystem.isOnRenderThread()) {
				RenderSystem.recordRenderCall(this::bind0);
			} else {
				this.bind0();
			}
		}

		private void bind0() {
			GLFW.glfwSetCursor(MinecraftClient.getInstance().getWindow().getHandle(), this.typeId.getAsLong());
		}
	}
}

package io.github.astrarre.gui.v1.api.listener.cursor;

import java.nio.file.Path;
import java.util.List;

import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Cursor {
	Key<List<Path>> FILES = (render, cursor, data) -> {}; // todo impl

	float x();

	float y();

	CursorType getType();

	void setType(CursorType type);

	<T> void set(@NotNull Key<T> key, T value);

	@Nullable
	<T> T get(@NotNull Key<T> key);

	boolean isPressed(ClickType type);

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


}

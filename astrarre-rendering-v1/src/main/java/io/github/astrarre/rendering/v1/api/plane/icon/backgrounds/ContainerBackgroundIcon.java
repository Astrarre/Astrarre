package io.github.astrarre.rendering.v1.api.plane.icon.backgrounds;

import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;

public record ContainerBackgroundIcon(float width, float height) implements Icon {
	@Override
	public void render(Render3d render) {
		// the background part
		render.fill().rect(0xffc6c6c6, 2, 2, width - 4, height - 4);
		// the top shiny part
		render.fill().rect(0xffffffff, 2, 1, width - 5, 2);
		// the left shiny part
		render.fill().rect(0xffffffff, 1, 2, 2, height - 5);
		// that one pixel in the top left
		render.fill().rect(0xffffffff, 3, 3, 1, 1);
		// the right shadow
		render.fill().rect(0xff555555, width - 3, 3, 2, height - 5);
		// the bottom shadow
		render.fill().rect(0xff555555, 3, height - 3, width - 5, 2);
		// that one pixel in the bottom right
		render.fill().rect(0xff555555, width - 4, height - 4, 1, 1);
		// the border
		render.fill().rect(0xff000000, 0, 2, 1, height - 5);
		render.fill().rect(0xff000000, 1, 1, 1, 1);
		render.fill().rect(0xff000000, 2, 0, width - 5, 1);
		render.fill().rect(0xff000000, width - 3, 1, 1, 1);
		render.fill().rect(0xff000000, width - 2, 2, 1, 1);
		render.fill().rect(0xff000000, width - 1, 3, 1, height - 5);
		render.fill().rect(0xff000000, 1, height - 3, 1, 1);
		render.fill().rect(0xff000000, 2, height - 2, 1, 1);
		render.fill().rect(0xff000000, 3, height - 1, width - 5, 1);
		render.fill().rect(0xff000000, width - 2, height - 2, 1, 1);
	}
}

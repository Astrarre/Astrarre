package io.github.astrarre.gui.v1.api.component;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.gui.v1.api.util.GuiRenderable;

public abstract class AHoverableComponent extends AComponent {
	public final Access<GuiRenderable> onHover = new Access<>("astrarre", "on_hover", array -> (a, b) -> {
		for(GuiRenderable renderable : array) {
			renderable.render(a, b);
		}
	});

	public AHoverableComponent() {
		this.post.put("hover", (cursor, render) -> {
			if(this.isIn(cursor)) {
				this.onHover.get().render(cursor, render);
			}
		});
	}
}

package io.github.astrarre.gui.v1.api.component;

import java.util.function.Consumer;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.gui.v1.api.util.GuiRenderable;
import io.github.astrarre.rendering.v1.api.plane.TooltipBuilder;
import org.jetbrains.annotations.NotNull;

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

	public AHoverableComponent tooltipDirect(@NotNull Consumer<TooltipBuilder> consumer) {
		this.onHover.andThen((cursor, render) -> {
			TooltipBuilder builder = render.tooltip();
			consumer.accept(builder);
			builder.render();
		});
		return this;
	}
}

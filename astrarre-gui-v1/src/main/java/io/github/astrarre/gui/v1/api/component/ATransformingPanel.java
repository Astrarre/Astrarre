package io.github.astrarre.gui.v1.api.component;

import io.github.astrarre.gui.v1.api.util.Transformed;

public abstract class ATransformingPanel extends APanel {
	@Override
	public APanel add(Transformed<?>... component) {
		var copied = new Transformed<?>[component.length];
		for(int i = 0; i < component.length; i++) {
			var c = component[i].component();
			copied[i] = this.transform(component[i], c.getWidth(), c.getHeight());
		}
		return super.add(copied);
	}

	public APanel add(AComponent component, float width, float height) {
		return super.add(this.transform(component, width, height));
	}

	protected abstract Transformed<?> transform(Transformed<?> current, float cw, float ch);
}

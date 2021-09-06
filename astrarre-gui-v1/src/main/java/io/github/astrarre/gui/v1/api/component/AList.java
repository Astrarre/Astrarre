package io.github.astrarre.gui.v1.api.component;

import io.github.astrarre.rendering.v1.api.util.Axis2d;
import io.github.astrarre.gui.v1.api.util.ComponentTransform;
import io.github.astrarre.rendering.v1.api.space.Transform3d;

/**
 * Spaces out components horizontally (or vertically)
 */
public class AList extends APanel {
	public final Axis2d axis;
	public final int spacing;
	float currentOffset;

	public AList(Axis2d axis) {
		this(axis, 0);
	}

	public AList(Axis2d axis, int spacing) {
		this.spacing = spacing;
		this.axis = axis;
	}



	@Override
	public APanel add(ComponentTransform<?>... component) {
		var copied = new ComponentTransform<?>[component.length];
		for(int i = 0; i < component.length; i++) {
			var c = component[i].component();
			copied[i] = this.transform(component[i], c.getWidth(), c.getHeight());
		}
		return super.add(copied);
	}

	public APanel add(AComponent component, float width, float height) {
		return super.add(this.transform(component, width, height));
	}

	protected ComponentTransform<?> transform(ComponentTransform<?> current, float cw, float ch) {
		var transform = Transform3d.translate(this.axis.x(this.currentOffset), this.axis.y(this.currentOffset), 0);
		Transform3d tr = current.transform();

		if(this.axis.isX()) {
			float xA = tr.transformX(cw, ch);
			float xB = tr.transformX(0, 0);
			float width = Math.max(xA, xB);
			this.currentOffset += width;
		} else {
			float yA = tr.transformY(cw, ch);
			float yB = tr.transformY(0, 0);
			float height = Math.max(yA, yB);
			this.currentOffset += height;
		}

		this.currentOffset += spacing;

		return current.before(transform);
	}
}

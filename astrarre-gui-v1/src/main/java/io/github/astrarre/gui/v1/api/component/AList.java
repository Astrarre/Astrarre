package io.github.astrarre.gui.v1.api.component;

import io.github.astrarre.gui.v1.api.component.AComponent;
import io.github.astrarre.gui.v1.api.component.APanel;
import io.github.astrarre.gui.v1.api.util.Transformed;
import io.github.astrarre.rendering.v1.api.util.Axis2d;
import io.github.astrarre.rendering.v1.api.space.Transform3d;

/**
 * Spaces out components horizontally (or vertically)
 */
public class AList extends ATransformingPanel {
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
	protected Transformed<?> transform(Transformed<?> current, float cw, float ch) {
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

		this.currentOffset += this.spacing;

		return current.before(transform);
	}
}

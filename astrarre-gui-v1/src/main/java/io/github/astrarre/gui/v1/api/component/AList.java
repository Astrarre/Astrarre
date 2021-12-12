package io.github.astrarre.gui.v1.api.component;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.v1.api.util.TransformedComponent;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import io.github.astrarre.rendering.v1.api.util.Axis2d;

/**
 * Spaces out components horizontally (or vertically)
 */
public class AList extends ATransformingPanel {
	public final Axis2d axis;
	public final int spacing;

	public AList(Axis2d axis) {
		this(axis, 0);
	}

	public AList(Axis2d axis, int spacing) {
		this.spacing = spacing;
		this.axis = axis;
	}

	@Override
	protected List<TransformedComponent> transformAll(List<TransformedComponent> originalComponents) {
		float currentOffset = 0;
		List<TransformedComponent> components = new ArrayList<>(originalComponents.size());
		for(TransformedComponent current : originalComponents) {
			float cw = current.component().getWidth(), ch = current.component().getHeight();
			var transform = Transform3d.translate(this.axis.x(currentOffset), this.axis.y(currentOffset), 0);
			Transform3d tr = current.transform();

			if(this.axis.isX()) {
				float xA = tr.transformX(cw, ch);
				float xB = tr.transformX(0, 0);
				float width = Math.max(xA, xB);
				currentOffset += width;
			} else {
				float yA = tr.transformY(cw, ch);
				float yB = tr.transformY(0, 0);
				float height = Math.max(yA, yB);
				currentOffset += height;
			}

			currentOffset += this.spacing;
			components.add(current.before(transform));
		}
		return components;
	}
}

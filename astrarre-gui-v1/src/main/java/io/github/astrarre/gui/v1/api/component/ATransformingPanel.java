package io.github.astrarre.gui.v1.api.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.astrarre.gui.v1.api.util.TransformedComponent;

public abstract class ATransformingPanel extends APanel {
	protected final List<TransformedComponent> original = new ArrayList<>();

	@Override
	public APanel add(TransformedComponent... component) {
		this.original.addAll(Arrays.asList(component));
		return super.add(component);
	}

	@Override
	public APanel remove(TransformedComponent... component) {
		this.original.removeAll(Arrays.asList(component));
		return super.remove(component);
	}

	@Override
	protected void recomputeBounds() {
		this.cmps.clear();
		this.cmps.addAll(this.transformAll(this.original));
		super.recomputeBounds();
	}

	protected abstract List<TransformedComponent> transformAll(List<TransformedComponent> originalComponents);
}

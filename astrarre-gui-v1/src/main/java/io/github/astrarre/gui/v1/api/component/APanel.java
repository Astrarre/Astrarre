package io.github.astrarre.gui.v1.api.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.github.astrarre.gui.v1.api.AComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class APanel extends AGroup {
	protected final List<AComponent> cmps = new ArrayList<>();

	@NotNull
	@Override
	public Iterator<AComponent> iterator() {
		return this.cmps.iterator();
	}

	@Override
	protected @Nullable AComponent before(AComponent component) {
		int index = this.cmps.indexOf(component);
		return index < 1 ? null : this.cmps.get(index - 1);
	}

	public APanel add(AComponent... component) {
		this.cmps.addAll(Arrays.asList(component));
		return this;
	}

	public APanel remove(AComponent... component) {
		this.cmps.addAll(Arrays.asList(component));
		return this;
	}
}

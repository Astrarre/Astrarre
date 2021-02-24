package io.github.astrarre.gui.v0.api;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public abstract class Container extends Drawable {
	protected final Object2IntMap<Drawable> componentRegistry = new Object2IntOpenHashMap<>();

	public void onClose() {
		ObjectIterator<Drawable> iterator = this.componentRegistry.keySet().iterator();
		while (iterator.hasNext()) {
			Drawable drawable = iterator.next();
			drawable.remove0();
			iterator.remove();
		}
	}
}

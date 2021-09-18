package io.github.astrarre.gui.internal.access;

import io.github.astrarre.gui.v1.api.component.ARootPanel;

public interface PanelScreenAccess {
	ARootPanel getRootPanel();

	void setRootPanel(ARootPanel panel);

	boolean hasRootPanel();
}

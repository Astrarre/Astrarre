package io.github.astrarre.testmod.gui;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.panel.APanel;

public class TestClientOnlyGui {
	public static void clientOnly() {
		RootContainer container = RootContainer.openClientOnly();
		APanel panel = container.getContentPanel();
		//panel.addClient(new BoundedDrawable());
	}

}

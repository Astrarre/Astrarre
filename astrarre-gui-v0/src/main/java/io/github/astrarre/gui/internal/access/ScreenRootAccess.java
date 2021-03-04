package io.github.astrarre.gui.internal.access;

import io.github.astrarre.gui.internal.PanelElement;
import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.networking.v0.api.io.Input;

public interface ScreenRootAccess {
	RootContainerInternal getRoot();

	void readRoot(Input input);

	void astrarre_focusPanel();
}

package io.github.astrarre.gui.v1.api;

import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.component.ARootPanel;

public interface Auditor {
	void auditClient(PacketHandler comms, ARootPanel panel) throws Throwable;
}
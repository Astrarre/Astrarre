package io.github.astrarre.gui.v1.api;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.gui.internal.slot.SlotAuditor;
import io.github.astrarre.util.v0.api.Id;

public class AstrarreGui {
	/**
	 * this is the maximum 'guaranteed' window in which you can render for GUIs
	 * In auto gui mode (in video settings) will rescale the coordinate grid to ensure that this 'window' in the center of the screen is always visible.
	 * For normal GUIs (centered guis, like inventories for example): it's recommended to use this scale.
	 */
	public static final int MAX_SAFE_WIDTH = 320, MAX_SAFE_HEIGHT = 240;

	/**
	 * post-process validation for server panels to catch common errors
	 */
	public static final Access<Auditor> AUDITORS = new Access<>(Id.create("astrarre", "auditor"), array -> (comms, panel) -> {
		for(var auditor : array) {
			auditor.auditClient(comms, panel);
		}
	});

	static {
		AUDITORS.andThen(new SlotAuditor());
	}
}

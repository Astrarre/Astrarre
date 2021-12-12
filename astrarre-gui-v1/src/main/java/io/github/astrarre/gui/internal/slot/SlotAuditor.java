package io.github.astrarre.gui.internal.slot;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.v1.api.Auditor;
import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.component.AComponent;
import io.github.astrarre.gui.v1.api.component.AGroup;
import io.github.astrarre.gui.v1.api.component.ARootPanel;
import io.github.astrarre.gui.v1.api.component.slot.ASlot;
import io.github.astrarre.gui.v1.api.component.slot.ASlotInternalAccess;
import io.github.astrarre.gui.v1.api.component.slot.SlotKey;
import io.github.astrarre.gui.v1.api.util.TransformedComponent;
import io.github.astrarre.util.v0.api.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SlotAuditor implements Auditor {
	private static final Logger LOGGER = LogManager.getLogger("SlotAuditor");
	@Override
	public void auditClient(PacketHandler comms, ARootPanel panel) throws Throwable {
		var list = new ArrayList<SlotKey>();
		this.searchSlots(panel, list);
		if(list.size() >= 2 && Validate.IS_DEV) {
			LOGGER.warn("Slot was not linked to any other slots, shift clicking will not work!");
			LOGGER.warn("You can safely ignore this message if the following slots are not meant to have shift-clicking support");
			for(SlotKey key : list) {
				LOGGER.warn("\t- " + key);
			}
		}
	}

	void searchSlots(AComponent component, List<SlotKey> keys) {
		if(component instanceof AGroup g) {
			for(TransformedComponent transform : g) {
				this.searchSlots(transform.component(), keys);
			}
		} else if(component instanceof ASlot a) {
			var key = ASlotInternalAccess.getKey(a);
			var linked = key.getKeys();
			if(linked.isEmpty()) {
				keys.add(key);
			}
		}
	}
}

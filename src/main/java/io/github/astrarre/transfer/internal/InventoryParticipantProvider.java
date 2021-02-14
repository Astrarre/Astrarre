package io.github.astrarre.transfer.internal;

import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;

public interface InventoryParticipantProvider {
	Participant<ItemKey> astrarre_getParticipant();
}

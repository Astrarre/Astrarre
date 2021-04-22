package io.github.astrarre.transfer.v0.api.player;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.ReplacingParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Hand;

public interface PlayerParticipant extends ArrayParticipant<ItemKey> {
	ReplacingParticipant<ItemKey> getHandReplacingParticipant(Hand hand);

	ReplacingParticipant<ItemKey> getCursorItemReplacingParticipant();

	/**
	 * attempts to insert the item into the player's inventory, if it is full, any remainder is dropped into the world
	 */
	void insertOrDrop(@Nullable Transaction transaction, ItemKey key, int amount);
}

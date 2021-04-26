package tests.lba;

import java.util.Collections;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.internal.compat.ShulkerboxItemParticipant;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.item.ItemSlotParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import net.devtech.potatounit.TestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.Bootstrap;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Items;

@RunWith (TestRunner.Client.class)
public class TransferTests {
	@Before
	public void bootstrap() {
		Bootstrap.initialize();
	}

	@Test
	public void slot() {
		ItemSlotParticipant participant = new ItemSlotParticipant();
		try(Transaction transaction = Transaction.create()) {
			participant.insert(transaction, ItemKey.of(Items.TNT), 10);
		}
		System.out.println(participant);
	}

	@Test // todo move out this has nothing to do with LBA
	public void shulkerTest() {
		ItemSlotParticipant participant = new ItemSlotParticipant(ItemKey.of(Items.SHULKER_BOX), 1);
		ArrayParticipant<ItemKey> array = () -> Collections.singletonList(participant);
		Participant<ItemKey> part = ShulkerboxItemParticipant.create(array.getSlotReplacingParticipant(0), ItemKey.of(Items.SHULKER_BOX), BlockEntityType.SHULKER_BOX);
		part.insert(null, ItemKey.of(Items.STONE), 4);
		System.out.println(participant);
	}
}

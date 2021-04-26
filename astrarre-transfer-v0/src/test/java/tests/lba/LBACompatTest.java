package tests.lba;

import java.util.Collections;

import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.internal.compat.ShulkerboxItemParticipant;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.item.ItemSlotParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.lba.item.ItemInsertableInsertable;
import net.devtech.potatounit.TestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.Bootstrap;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@RunWith(TestRunner.Client.class)
public class LBACompatTest {
	@Before
	public void bootstrap() {
		Bootstrap.initialize();
	}

	@Test
	public void test() {
		FullFixedItemInv insertable = new FullFixedItemInv(1);
		ItemInsertableInsertable ins = new ItemInsertableInsertable(insertable);
		try(Transaction transaction = Transaction.create()) {
			Assert.assertEquals(10, ins.insert(transaction, ItemKey.of(Items.TNT), 10));
			Assert.assertEquals(10, ins.insert(transaction, ItemKey.of(Items.TNT), 10));
			Assert.assertEquals(0, ins.insert(transaction, ItemKey.of(Items.STONE), 10));
		}

		Assert.assertTrue(ItemStack.areEqual(insertable.getInvStack(0), new ItemStack(Items.TNT, 20)));
	}

	@Test
	public void shulkerTest() {
		ItemSlotParticipant participant = new ItemSlotParticipant();
		participant.insert(null, ItemKey.of(Items.SHULKER_BOX), 1);
		ArrayParticipant<ItemKey> array = () -> Collections.singletonList(participant);
		Participant<ItemKey> part = ShulkerboxItemParticipant.create(array.getSlotReplacingParticipant(0), ItemKey.of(Items.SHULKER_BOX), BlockEntityType.SHULKER_BOX);
		part.insert(null, ItemKey.of(Items.STONE), 4);
		System.out.println(participant);
	}

	@Test
	public void queryTest() {

	}
}

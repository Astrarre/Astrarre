package tests.lba;

import alexiil.mc.lib.attributes.item.ItemInsertable;
import alexiil.mc.lib.attributes.item.SingleItemSlot;
import alexiil.mc.lib.attributes.item.impl.DirectFixedItemInv;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.lba.ItemInsertableInsertable;
import net.devtech.potatounit.TestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@RunWith(TestRunner.Client.class)
public class ItemInsertableInsertableTests {
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
		Assert.assertEquals(new ItemStack(Items.TNT, 20), insertable.getInvStack(0));
	}
}

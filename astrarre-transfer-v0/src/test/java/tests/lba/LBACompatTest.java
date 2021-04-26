package tests.lba;

import java.util.Collections;

import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.internal.compat.ShulkerboxItemParticipant;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.Participants;
import io.github.astrarre.transfer.v0.api.item.ItemSlotParticipant;
import io.github.astrarre.transfer.v0.api.participants.array.ArrayParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.transfer.v0.lba.fluid.LBAFluidsCompat;
import io.github.astrarre.transfer.v0.lba.item.ItemInsertableInsertable;
import io.github.astrarre.transfer.v0.lba.item.LBAItemsCompat;
import net.devtech.potatounit.TestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.Bootstrap;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@RunWith (TestRunner.Client.class)
public class LBACompatTest {
	@Before
	public void bootstrap() {
		Bootstrap.initialize();
		LBAItemsCompat.init();
		LBAFluidsCompat.init();
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

		ItemStack stack = insertable.getInvStack(0);
		Assert.assertEquals(Items.TNT, stack.getItem());
		Assert.assertEquals(20, stack.getCount());
	}

	// todo use some other testing framework for in-world testing, or add to taterunit

	/**
	 * I have no glass bottle compat *yet* so I'm using this to test bidirectional compatibility
	 */
	@Test
	public void itemQueryTest() {
		ItemSlotParticipant participant = new ItemSlotParticipant(ItemKey.of(Items.GLASS_BOTTLE), 1);
		ArrayParticipant<ItemKey> array = () -> Collections.singletonList(participant);
		Participant<Fluid> part = FabricParticipants.FLUID_ITEM.get().get(null, ItemKey.of(Items.GLASS_BOTTLE), 1, array.getSlotReplacingParticipant(0));
		int bottle = part.insert(Transaction.GLOBAL, Fluids.WATER, Droplet.BOTTLE);
		Assert.assertEquals(Droplet.BOTTLE, bottle);
	}
}

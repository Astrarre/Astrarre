package tests.item;

import static org.junit.Assert.*;

import io.github.astrarre.transfer.internal.SlotParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import org.junit.Before;
import org.junit.Test;

import net.minecraft.Bootstrap;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class SlotParticipantTest {
	@Before
	public void init() {
		Bootstrap.initialize();
	}

	@Test
	public void insert() {
		// todo classloader magic
		Inventory inventory = new SimpleInventory(9);
		SlotParticipant participant = new SlotParticipant(inventory, 5);

		try (Transaction transaction = new Transaction()) {
			participant.insert(transaction, io.github.astrarre.v0.item.Items.STONE, 5);
		}

		ItemStack at = inventory.getStack(5);
		assertEquals("Unexpected Item", Items.STONE, at.getItem());
		assertEquals("Unexpected Count", 5, at.getCount());
	}
}

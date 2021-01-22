package tests.fluid;

import static org.junit.Assert.*;

import clsgolf.ClasspathTestRunner;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.participants.fluid.FluidVolume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.Bootstrap;
import net.minecraft.fluid.Fluids;

@RunWith(ClasspathTestRunner.class)
public class FluidTest {
	@Before
	public void bootstrap() {
		Bootstrap.initialize();
	}

	@Test
	public void testCommit() {
		FluidVolume container = new FluidVolume(Fluids.EMPTY, 0);
		try(Transaction transaction = new Transaction()) {
			assertEquals(100, container.insert(transaction, Fluids.WATER, 100));
		}
		assertEquals(100, container.extract(null, Fluids.WATER, 100));
	}

	@Test
	public void testAbort() {
		FluidVolume container = new FluidVolume(Fluids.EMPTY, 0);
		try(Transaction transaction = new Transaction(false)) {
			assertEquals(100, container.insert(transaction, Fluids.WATER, 100));
		}
		assertEquals(0, container.extract(null, Fluids.WATER, 100));
	}

	@Test
	public void testAbortCommit() {
		FluidVolume container = new FluidVolume(Fluids.EMPTY, 0);
		try (Transaction transaction = new Transaction()) {
			assertEquals(100, container.insert(transaction, Fluids.WATER, 100));

			try (Transaction inner = new Transaction(false)) {
				assertEquals(100, container.extract(inner, Fluids.WATER, 100));
			}

			assertEquals(100, container.extract(transaction, Fluids.WATER, 100));
		}
	}

	@Test
	public void testCommitAbort() {
		FluidVolume container = new FluidVolume(Fluids.WATER, 100);
		try (Transaction transaction = new Transaction(false)) {
			assertEquals(100, container.insert(transaction, Fluids.WATER, 100));

			try (Transaction inner = new Transaction()) {
				assertEquals(200, container.extract(inner, Fluids.WATER, 200));
			}

			assertTrue(container.isEmpty(transaction));
		}

		assertEquals(100, container.extract(null, Fluids.WATER, 100));
	}
}

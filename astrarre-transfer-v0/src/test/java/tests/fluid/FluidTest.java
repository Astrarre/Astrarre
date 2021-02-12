package tests.fluid;

import static org.junit.Assert.*;

import clsgolf.ClasspathTestRunner;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.api.participants.fluid.FluidVolume;
import io.github.astrarre.v0.fluid.Fluids;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.Bootstrap;

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
			assertEquals(100, container.insert(transaction, Fluids.EMPTY, 100));
		}
		assertEquals(100, container.extract(null, Fluids.EMPTY, 100));
	}

	@Test
	public void testAbort() {
		FluidVolume container = new FluidVolume(Fluids.EMPTY, 0);
		try(Transaction transaction = new Transaction(false)) {
			assertEquals(100, container.insert(transaction, Fluids.EMPTY, 100));
		}
		assertEquals(0, container.extract(null, Fluids.EMPTY, 100));
	}

	@Test
	public void testAbortCommit() {
		FluidVolume container = new FluidVolume(Fluids.EMPTY, 0);
		try (Transaction transaction = new Transaction()) {
			assertEquals(100, container.insert(transaction, Fluids.EMPTY, 100));

			try (Transaction inner = new Transaction(false)) {
				assertEquals(100, container.extract(inner, Fluids.EMPTY, 100));
			}

			assertEquals(100, container.extract(transaction, Fluids.EMPTY, 100));
		}
	}

	@Test
	public void testCommitAbort() {
		FluidVolume container = new FluidVolume(Fluids.EMPTY, 100);
		try (Transaction transaction = new Transaction(false)) {
			assertEquals(100, container.insert(transaction, Fluids.EMPTY, 100));

			try (Transaction inner = new Transaction()) {
				assertEquals(200, container.extract(inner, Fluids.EMPTY, 200));
			}

			assertTrue(container.isEmpty(transaction));
		}

		assertEquals(100, container.extract(null, Fluids.EMPTY, 100));
	}
}

package tests.lba;

import net.devtech.potatounit.TestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import reborncore.common.blockentity.FluidConfiguration;

import net.minecraft.Bootstrap;

@RunWith (TestRunner.Client.class)
public class TRCompatTest {
	@Before
	public void bootstrap() {
		Bootstrap.initialize();
	}

	@Test
	public void loadMixin() {
		System.out.println(new FluidConfiguration());
	}
}

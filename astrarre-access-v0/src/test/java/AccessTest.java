import java.util.function.Function;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.func.IterFunc;
import net.devtech.potatounit.TestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TestRunner.Client.class)
public class AccessTest {
	public Access<Function<String, Integer>> testAccess;
	@Before
	public void setup() {
		this.testAccess = new Access<>(Id.create("access-test", "test"), IterFunc.NON_NULL);
	}

	@Test
	public void register() {
		this.testAccess.andThen(s -> {
			if(s.contains("hello")) {
				return 1;
			}
			return 0;
		});
		Assert.assertEquals(this.testAccess.get().apply("hello there!"), new Integer(1));
		Assert.assertEquals(this.testAccess.get().apply("general kenobi!"), new Integer(0));
	}
}

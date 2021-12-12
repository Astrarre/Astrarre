import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.util.v0.api.Id;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

public class CircularTest {
	public FunctionAccess<String, Integer> legacyAPI;
	public FunctionAccess<String, Float> newAPI;

	@Before
	public void setup() {
		this.legacyAPI = new FunctionAccess<>(Id.create("access-test", "a"));
		this.newAPI = new FunctionAccess<>(Id.create("access-test", "b"));
		this.legacyAPI.dependsOn(this.newAPI, function -> s -> {
			Float f = function.apply(s);
			return f != null ? f.intValue() : null;
		});
		this.newAPI.dependsOn(this.legacyAPI, function -> s -> {
			Integer i = function.apply(s);
			return i != null ? i.floatValue() : null;
		});

		this.legacyAPI.andThen(s -> {
			if(s.contains("test")) {
				return 4;
			}
			return null;
		});

		this.newAPI.andThen(s -> {
			if(s.contains("hell")) {
				return 666f;
			}
			return null;
		});
	}

	@Test
	public void register() {
		Assert.assertEquals(this.legacyAPI.get().apply("hello"), 666f, .01);
		Assert.assertEquals(this.newAPI.get().apply("test"), 4, .01);
	}
}

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.common.reflect.TypeToken;
import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.access.v0.fabric.helper.TypeAccessHelper;
import io.github.astrarre.util.v0.api.Id;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TypeAccessTest {
	public FunctionAccess<Object, Integer> testAccess;
	public TypeAccessHelper<Object, Function<Object, Integer>> helper;

	@Before
	public void setup() {
		this.testAccess = new FunctionAccess<>(Id.create("access-test", "test"));
		this.helper = new TypeAccessHelper<>(this.testAccess, function -> o -> function.apply(o.getClass()).apply(o));
	}

	@Test
	public void register() {
		this.helper.forTypeGeneric(new TypeToken<List<String>>() {}, o -> {
			List<String> test = (List<String>) o;
			return test.size();
		});

		List<String> example = new ArrayList<>() {};
		example.add("hello");
		Assert.assertEquals(this.testAccess.get().apply(example), Integer.valueOf(1));
	}
}

package io.github.astrarre.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.astrarre.access.v0.api.Provider;
import io.github.astrarre.access.v0.api.func.Access;
import org.junit.jupiter.api.Test;

public class ProviderTest {
	@Test
	public void testWrap() {
		Provider<IntFunction<Integer>, Integer> integerProvider = new Provider<>(IntFunction::andThen, i -> null);
		Provider<FloatFunction<Integer>, Integer> floatProvider = new Provider<>(FloatFunction::andThen, i -> null);
		assertNull(integerProvider.get().apply(0));
		floatProvider.andThen(f -> (int) f);
		assertEquals(5, floatProvider.get().apply(5.5f));
		integerProvider.<FloatFunction<Integer>>wraps((float i) -> floatProvider.get().apply(i), f -> f::apply);
		assertEquals(5, integerProvider.get().apply(5));
	}

	public interface IntFunction<A> extends Access<A> {
		default IntFunction<A> andThen(IntFunction<A> function) {
			return a -> {
				A i = function.apply(a);
				if (i == null) {
					return function.apply(a);
				}
				return i;
			};
		}

		A apply(int a);
	}

	public interface FloatFunction<A> extends Access<A> {
		default FloatFunction<A> andThen(FloatFunction<A> function) {
			return a -> {
				A i = function.apply(a);
				if (i == null) {
					return function.apply(a);
				}
				return i;
			};
		}

		A apply(float a);
	}
}

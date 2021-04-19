package io.github.astrarre.access.v0.api.provider;

import io.github.astrarre.access.v0.api.Access;
import org.jetbrains.annotations.Nullable;

/**
 * providers are implemented on the object being accessed. For example {@code FunctionAccess<SomeClass, Integer>}, you would {@code implement
 * Provider} on {@code SomeClass} (or some subclass of) and return an integer (or null if it's the wrong access or you don't want to return null)
 */
@FunctionalInterface
public interface Provider {
	/**
	 * <pre> {@code
	 *  public class MyProvider implements Provider {
	 *      @Override
	 *      public Object get(Access<?> access) {
	 *          if(access == MyAccesses.SOME_ACCESS) // this is how you know the type information, you're can see what SOME_ACCESS wants
	 *              return <some value>;
	 *          return null;
	 *      }
	 *  }
	 * }
	 * </pre>
	 *
	 * @param access the provider accessing this object
	 * @return whatever value the access is supposed to want
	 */
	@Nullable Object get(Access<?> access);
}
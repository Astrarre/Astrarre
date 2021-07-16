package io.github.astrarre.access.v0.api.provider;

import io.github.astrarre.access.v0.api.Access;
import org.jetbrains.annotations.Nullable;

/**
 * providers are implemented on the object being accessed. For example {@code FunctionAccess<SomeClass, Integer>}, you would {@code implement
 * GenericProvider} on {@code SomeClass} (or some subclass of) and return a `Function<SomeClass, Integer>`
 */
@FunctionalInterface
public interface GenericProvider<F> {
	/**
	 * <pre> {@code
	 *  public class MyProvider implements GenericProvider<WorldFunction<Inventory>> {
	 *      @Override
	 *      public WorldFunction<Inventory> get(Access<WorldFunction<Inventory>> access) {
	 *          // make sure the access is the right one,
	 *          if(access == MyAccesses.SOME_ACCESS)
	 *              return (...) -> {...};
	 *          return null;
	 *      }
	 *  }
	 * }
	 * </pre>
	 *
	 * Keep in mind that {@code access} may not actually be of type Access<F>, we're just abusing type erasure here.
	 *
	 * @param access the provider accessing this object
	 * @return whatever value the access is supposed to want
	 */
	@Nullable F get(Access<F> access);
}

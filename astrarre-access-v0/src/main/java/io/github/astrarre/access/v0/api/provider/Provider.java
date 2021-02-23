package io.github.astrarre.access.v0.api.provider;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.func.Returns;
import org.jetbrains.annotations.Nullable;

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
package io.github.astrarre.access.v0.api;

import java.util.function.BinaryOperator;

import io.github.astrarre.access.v0.api.func.Access;
import org.jetbrains.annotations.NotNull;

public class RegistryProvider<A extends Access<T>, T> extends Provider<A, T> {
	public RegistryProvider(BinaryOperator<A> andThen, A defaultAccess) {
		super(andThen, defaultAccess);
	}

	/**
	 * @deprecated use the documented access version
	 */
	@Override
	@Deprecated
	public @NotNull A get() {
		return super.get();
	}

}

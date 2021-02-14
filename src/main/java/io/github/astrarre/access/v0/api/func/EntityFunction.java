package io.github.astrarre.access.v0.api.func;

import java.util.function.BinaryOperator;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.v0.entity.Entity;
import io.github.astrarre.v0.util.math.Box;
import io.github.astrarre.v0.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public interface EntityFunction<T> extends Returns<T> {
	// todo caching
	T get(@Nullable Direction direction, Entity entity);

	static <T> EntityFunction<@Nullable T> empty() {
		return (direction, entity) -> null;
	}

	/**
	 * don't forget to declare the provider as a dependency if you use this to register a provider into another provider!
	 * @see Access#andThen(Returns, Access[])
	 * @see Access#addDependency(Access)
	 */
	static <T> EntityFunction<T> of(Access<EntityFunction<T>, T> access) {
		return (direction, entity) -> access.get().get(direction, entity);
	}

	/**
	 * @return a world function that queries all of the entities that collide with the queried position, and returns the first non-value.
	 */
	default WorldFunction<T> toWorldFunction() {
		return (direction, state, view, pos, entity) -> {
			for (Entity otherEntity : view.getOtherEntities(null, Box.newInstance(pos))) {
				T inv = this.get(direction, otherEntity);
				if(inv != null) {
					return inv;
				}
			}
			return null;
		};
	}

	/**
	 * @return a world function that queries all of the entities that collide with the queried position, and merges all returned non-null values with the given function
	 */
	default WorldFunction<T> toWorldFunction(BinaryOperator<T> combiner) {
		return (direction, state, view, pos, entity) -> {
			T result = null;
			for (Entity otherEntity : view.getOtherEntities(null, Box.newInstance(pos))) {
				T val = this.get(direction, otherEntity);
				if(val != null) {
					if(result == null) {
						result = val;
					} else {
						result = combiner.apply(result, val);
					}
				}
			}
			return result;
		};
	}

	default EntityFunction<T> andThen(EntityFunction<T> function) {
		return (direction, entity) -> {
			T val = this.get(direction, entity);
			if(val != null) {
				return val;
			}
			return function.get(direction, entity);
		};
	}
}

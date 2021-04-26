package io.github.astrarre.access.v0.fabric.func;

import java.util.function.BinaryOperator;

import io.github.astrarre.util.v0.api.func.IterFunc;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

/**
 *
 * @param <T>
 */
public interface EntityFunction<T> {
	// todo caching
	T get(@Nullable Direction direction, Entity entity);

	static <T> IterFunc<EntityFunction<T>> skipIfNull(T defaultValue) {
		return (functions) -> (d, e) -> {
			for (EntityFunction<T> function : functions) {
				T val = function.get(d, e);
				if (val != null) {
					return val;
				}
			}
			return defaultValue;
		};
	}

	static <T> EntityFunction<@Nullable T> empty() {
		return (direction, entity) -> null;
	}

	/**
	 * @return a world function that queries all of the entities that collide with the queried position, and returns the first non-value.
	 */
	default WorldFunction<T> toWorldFunction() {
		return (direction, state, view, pos, entity) -> {
			for (Entity otherEntity : view.getOtherEntities(null, new Box(pos))) {
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
			for (Entity otherEntity : view.getOtherEntities(null, new Box(pos))) {
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

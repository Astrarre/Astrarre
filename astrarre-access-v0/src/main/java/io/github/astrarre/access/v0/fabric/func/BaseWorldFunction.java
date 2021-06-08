package io.github.astrarre.access.v0.fabric.func;

import java.lang.reflect.Proxy;

import io.github.astrarre.access.internal.SkippingWorldFunction;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * To optimize a WorldFunction query, you want to avoid getting blockstate and getting blockentity when none of the functions need it.
 *
 * If you inherit this function, {@link #optimizeQuery(BaseWorldFunction, boolean, boolean)} can be used to create instances of your function without needing to create subinterfaces
 * for every combination like {@link WorldFunction.NoBlock} & {@link WorldFunction.NoBlockEntity} & {@link WorldFunction.NoBlockState}.
 *
 * @see WorldFunction
 */
public interface BaseWorldFunction {
	/**
	 * This isn't optimized so it's recommended you create your own version for your specific world function.
	 * @param needsBlockState if false, the passed block state should be assumed to be null
	 * @param needsBlockEntity if false, the passed block entity should be assumed to be null
	 * @return a base world function of the passed type that implements needsBlockState and needsBlockEntity.
	 */
	@SuppressWarnings ("unchecked")
	static <T extends BaseWorldFunction> T optimizeQuery(T function, boolean needsBlockState, boolean needsBlockEntity) {
		Class<?> cls = function.getClass();
		return (T) Proxy.newProxyInstance(cls.getClassLoader(), new Class[] {cls}, (proxy, method, args) -> {
			if (method.getReturnType() == boolean.class && method.getParameterCount() == 0) {
				switch (method.getName()) {
				case "needsBlockState":
					return needsBlockState;
				case "needsBlockEntity":
					return needsBlockEntity;
				}
			}
			return method.invoke(proxy, args);
		});
	}

	default boolean needsBlockState() {return true;}

	default boolean needsBlockEntity() {return true;}

	/**
	 * utility function to assist in implementing world queries.
	 * @see SkippingWorldFunction#get(Direction, BlockState, World, BlockPos, BlockEntity, boolean)
	 */
	static BlockEntity queryBlockEntity(BaseWorldFunction function, @Nullable BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity, boolean hasBlockEntity) {
		if (!hasBlockEntity && function.needsBlockEntity()) {
			if (state == null || state.hasBlockEntity()) {
				return world.getBlockEntity(pos);
			} else {
				return null;
			}
		}
		return entity;
	}

	/**
	 * utility function to assist in implementing world queries.
	 * @see SkippingWorldFunction#get(Direction, BlockState, World, BlockPos, BlockEntity, boolean)
	 */
	static BlockState queryBlockState(BaseWorldFunction function, @Nullable BlockState state, World world, BlockPos pos, @Nullable BlockEntity entity) {
		if (state == null && function.needsBlockState()) {
			if (entity != null) {
				return entity.getCachedState();
			} else {
				return world.getBlockState(pos);
			}
		}
		return state;
	}
}

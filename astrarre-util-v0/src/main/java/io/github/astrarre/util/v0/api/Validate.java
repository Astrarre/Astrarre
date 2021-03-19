package io.github.astrarre.util.v0.api;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.launch.common.FabricLauncherBase;

public class Validate {
	public static final FabricLoader LOADER = FabricLoader.getInstance();
	public static final boolean IS_DEV = (FabricLauncherBase.getLauncher() == null || LOADER.isDevelopmentEnvironment()) && !Boolean.getBoolean("astrarre-disable-debug");
	public static void void_(Object object) {}

	/**
	 * @throws T rethrows {@code throwable}
	 * @return nothing, because it throws
	 */
	@SuppressWarnings ("unchecked")
	public static <T extends Throwable> RuntimeException rethrow(Throwable throwable) throws T {
		throw (T) throwable;
	}

	/**
	 * @param name the name of the parameter
	 * @return {@code val}
	 * @throws IllegalArgumentException if {@code val} < 0
	 */
	public static int positive(int val, String name) {
		return greaterThanEqualTo(val, 0, name);
	}

	/**
	 * @param name the name of the parameter
	 * @return {@code val}
	 * @throws IllegalArgumentException if {@code val} < {@code comp}
	 */
	public static int greaterThanEqualTo(int val, int comp, String name) {
		if (val >= comp) {
			return val;
		}
		throw new IllegalArgumentException(String.format("%s (%d) < %d!", name, val, comp));
	}

	/**
	 * @param name the name of the parameter
	 * @return {@code val}
	 * @throws IllegalArgumentException if {@code val} <= {@code comp}
	 */
	public static int greaterThan(int val, int comp, String name) {
		if (val > comp) {
			return val;
		}
		throw new IllegalArgumentException(String.format("%s (%d) <= %d!", name, val, comp));
	}

	public static <T> T notNull(T object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
		return object;
	}

	public static <A, B> B instanceOf(A object, Class<B> cls, String message) {
		if (cls.isInstance(object)) {
			return (B) object;
		}
		throw new IllegalArgumentException(message);
	}

	public static <A, B> B filter(A a, Class<B> cls) {
		if (cls.isInstance(a)) {
			return (B) a;
		} else {
			return null;
		}
	}

	@Nullable
	public static <A> A filter(A obj, Predicate<A> a) {
		if (a.test(obj)) {
			return obj;
		} else {
			return null;
		}
	}

	public static void isTrue(boolean va, String msg) {
		if (!va) {
			throw new IllegalArgumentException(msg);
		}
	}

	public static void isNull(Object value, String error) {
		if(value != null) {
			throw new IllegalArgumentException(error);
		}
	}

	public static void lessThan(int index, int length, String s) {
		if (index >= length) {
			throw new IllegalArgumentException(s);
		}
	}
}

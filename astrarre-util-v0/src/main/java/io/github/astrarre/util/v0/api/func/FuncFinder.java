package io.github.astrarre.util.v0.api.func;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

import io.github.astrarre.util.v0.api.Validate;

public interface FuncFinder {
	static FuncFinder byName(String name) {
		return byName(name, true);
	}

	static FuncFinder byName(String name, boolean isVisible) {
		return only(m -> m.getName().equals(name), isVisible);
	}

	static FuncFinder exact(String name, Class<?>... args) {
		return cls -> {
			try {
				return cls.getDeclaredMethod(name, args);
			} catch(NoSuchMethodException e) {
				throw Validate.rethrow(e);
			}
		};
	}

	Method find(Class<?> cls);

	static FuncFinder onlyAbstract() {
		return onlyAbstract(true);
	}

	static FuncFinder onlyAbstract(boolean visible) {
		return only(m -> Modifier.isAbstract(m.getModifiers()), visible);
	}

	static FuncFinder only(Predicate<Method> test, boolean visible) {
		return cls -> {
			Method m = null;
			if(visible) {
				for(Method method : cls.getMethods()) {
					if(test.test(method)) {
						Validate.isNull(m, "multiple methods match predicate!");
						m = method;
					}
				}
			} else {
				Class<?> curr = cls;
				while(curr != null) {
					for(Method method : curr.getDeclaredMethods()) {
						if(test.test(method)) {
							Validate.isNull(m, "multiple methods match predicate!");
							m = method;
						}
					}
					curr = curr.getSuperclass(); // interfaces can't hide methods
				}
			}
			return m;
		};
	}
}

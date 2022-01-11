package io.github.astrarre.recipe.internal;

import java.lang.reflect.Proxy;

import io.github.astrarre.recipe.v0.api.Reloadable;

public class ReloadableInternals {
	@SuppressWarnings("unchecked")
	public static <T extends Reloadable<?>> T delegateType(Reloadable<?> instance, Class<? super T> type) {
		return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] {type}, (proxy, method, args) -> {
			Class<?> declare = method.getDeclaringClass();
			if(declare.isAssignableFrom(type) && declare != Object.class) {
				return method.invoke(instance.get(), args);
			} else {
				return method.invoke(instance, args);
			}
		});
	}
}

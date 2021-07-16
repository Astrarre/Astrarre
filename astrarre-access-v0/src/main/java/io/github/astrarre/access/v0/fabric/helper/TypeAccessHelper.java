package io.github.astrarre.access.v0.fabric.helper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.reflect.TypeToken;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.MapFilter;
import io.github.astrarre.access.v0.api.helper.AbstractAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

public class TypeAccessHelper<T, F> extends AbstractAccessHelper<Class<? extends T>, F> {
	protected final MapFilter<Class<? extends T>, F> filterClassExact;
	protected final MapFilter<Class<? super T>, F> filterSuperClass, filterInterfaces;
	protected final MapFilter<ParameterizedType, F> filterSuperType;

	public TypeAccessHelper(AbstractAccessHelper<Class<? extends T>, F> copyFrom) {
		this(copyFrom.iterFunc, copyFrom.andThen, copyFrom.empty);
	}

	public TypeAccessHelper(AbstractAccessHelper<?, F> copyFrom, Consumer<Function<Class<? extends T>, F>> adder) {
		this(copyFrom.iterFunc, adder, copyFrom.empty);
	}

	public TypeAccessHelper(Access<F> access, Function<Function<Class<? extends T>, F>, F> transformer) {
		this(access.combiner, func -> access.andThen(transformer.apply(func)), access.combiner.empty());
	}

	public TypeAccessHelper(IterFunc<F> func, Consumer<Function<Class<? extends T>, F>> adder, F empty) {
		super(func, adder, empty);
		this.filterClassExact = new MapFilter<>(func, empty);
		this.filterSuperClass = new MapFilter<>(func, empty);
		this.filterInterfaces = new MapFilter<>(func, empty);
		this.filterSuperType = new MapFilter<>(func, empty);
	}

	public TypeAccessHelper<T, F> forClassExact(Class<? extends T> type, F func) {
		if(this.filterClassExact.add(type, func)) {
			this.andThen.accept(this.filterClassExact::get);
		}
		return this;
	}

	public TypeAccessHelper<T, F> forClass(Class<? super T> type, F func) {
		if(type.isInterface()) {
			if(this.filterInterfaces.add(type, func)) {
				this.andThen.accept(this::getInterfaceFunction);
			}
		} else {
			if(this.filterSuperClass.add(type, func)) {
				this.andThen.accept(c -> {
					Class<? super T> current = (Class<? super T>) c;
					while(current != null) {
						F found = this.filterSuperClass.get(current);
						if(found != this.empty) {
							return found;
						}
						current = current.getSuperclass();
					}
					return this.empty;
				});
			}
		}
		return this;
	}

	public TypeAccessHelper<T, F> forTypeGeneric(TypeToken<? extends T> token, F func) {
		Type type = token.getType();
		if(type instanceof Class c) {
			return this.forClass(c, func);
		} else if(type instanceof ParameterizedType p) {
			return this.forTypeGeneric(p, func);
		} else {
			throw new UnsupportedOperationException("Unknown type " + type);
		}
	}

	public TypeAccessHelper<T, F> forTypeGeneric(ParameterizedType type, F func) {
		if(((Class<?>)type.getRawType()).isInterface()) {
			TypeToken<?> token = TypeToken.of(type);
			this.andThen.accept(c -> {
				if(token.isSupertypeOf(c.getGenericSuperclass())) {
					return func;
				}
				return this.empty;
			});
		} else {
			if(this.filterSuperType.add(type, func)) {
				this.andThen.accept(c -> {
					Type current = c.getGenericSuperclass();
					while(current != null) {
						if(current instanceof ParameterizedType paramType) {
							F found = this.filterSuperType.get(paramType);
							if(found != this.empty) {
								return found;
							}

						} else {
							current = ((Class<?>) current).getSuperclass();
						}
					}
					return this.empty;
				});
			}
		}
		return this;
	}

	private F getInterfaceFunction(Class<? extends T> c) {
		if(this.filterInterfaces.size() < 20) {
			for(Map.Entry<Class<? super T>, F> function : this.filterInterfaces.functions()) {
				if(function.getKey().isAssignableFrom(c)) {
					F found = function.getValue();
					if(found != this.empty) {
						return found;
					}
				}
			}
		} else {
			for(Class<?> iface : c.getInterfaces()) {
				F found = this.filterInterfaces.get((Class<? super T>) iface);
				if(found != this.empty) {
					return found;
				}
			}
		}

		return this.empty;
	}
}

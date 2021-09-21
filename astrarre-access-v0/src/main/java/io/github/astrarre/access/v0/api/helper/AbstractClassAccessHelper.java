package io.github.astrarre.access.v0.api.helper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.reflect.TypeToken;
import io.github.astrarre.access.internal.TypeHelper;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.MapFilter;
import io.github.astrarre.access.v0.api.util.FilteredFunc;
import io.github.astrarre.util.v0.api.func.IterFunc;

public abstract class AbstractClassAccessHelper<T, C extends Type, F> {
	public final IterFunc<F> iterFunc;
	public final FilteredFunc.Adding<C, F> andThen;
	public final F empty;
	protected final MapFilter<Class<?>, F> filterClassExact;
	protected final MapFilter<Class<?>, F> instanceOfSuper, instanceOfInterface;
	protected final MapFilter<ParameterizedType, F> instanceOfGeneric;

	public AbstractClassAccessHelper(AccessHelpers.Context<C, F> context) {
		this(context.func(), context.andThen(), context.empty());
	}

	public AbstractClassAccessHelper(IterFunc<F> func, FilteredFunc.Adding<C, F> adder, F empty) {
		this.iterFunc = func;
		this.andThen = adder;
		this.empty = empty;
		this.filterClassExact = new MapFilter<>(func, empty);
		this.instanceOfSuper = new MapFilter<>(func, empty);
		this.instanceOfInterface = new MapFilter<>(func, empty);
		this.instanceOfGeneric = new MapFilter<>(func, empty);
	}

	protected abstract C convert(Type type);

	public AbstractClassAccessHelper<T, C, F> forClassExact(Class<? extends T> type, F func) {
		if(this.filterClassExact.add(type, func)) {
			this.andThen.accept(c -> this.filterClassExact.get(TypeHelper.raw(c)));
		}
		return this;
	}

	public AbstractClassAccessHelper<T, C, F> forClass(Class<? super T> type, F func) {
		if(type.isInterface()) {
			if(this.instanceOfInterface.add(type, func)) {
				this.andThen.accept(c -> this.getInterfaceFunction(TypeHelper.raw(c)));
			}
		} else {
			if(this.instanceOfSuper.add(type, func)) {
				this.andThen.accept(c -> {
					Class<? super C> current = (Class<? super C>) c;
					while(current != null) {
						F found = this.instanceOfSuper.get(current);
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


	public AbstractClassAccessHelper<T, C, F> forTypeGeneric(TypeToken<? extends T> token, F func) {
		Type type = token.getType();
		if(type instanceof Class c) {
			return this.forClass(c, func);
		} else if(type instanceof ParameterizedType p) {
			return this.forTypeGeneric(p, func);
		} else {
			throw new UnsupportedOperationException("Unknown type " + type);
		}
	}


	public AbstractClassAccessHelper<T, C, F> forTypeGeneric(ParameterizedType type, F func) {
		if(((Class<?>) type.getRawType()).isInterface()) {
			TypeToken<?> token = TypeToken.of(type);
			this.andThen.accept(c -> {
				if(token.isSupertypeOf(c)) {
					return func;
				}
				return this.empty;
			});
		} else if(this.instanceOfGeneric.add(type, func)) {
			this.andThen.accept(c -> {
				Type current = c;
				while(current != null) {
					if(current instanceof ParameterizedType paramType) {
						F found = this.instanceOfGeneric.get(paramType);
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
		return this;
	}

	private F getInterfaceFunction(Class<?> c) {
		if(this.instanceOfInterface.size() < 20) {
			for(var function : this.instanceOfInterface.functions()) {
				if(function.getKey().isAssignableFrom(c)) {
					F found = function.getValue();
					if(found != this.empty) {
						return found;
					}
				}
			}
		} else {
			for(Class<?> iface : c.getInterfaces()) {
				F found = this.instanceOfInterface.get(iface);
				if(found != this.empty) {
					return found;
				} else {
					found = this.getInterfaceFunction(iface);
					if(found != this.empty) {
						return found;
					}
				}
			}
		}
		return this.empty;
	}
}

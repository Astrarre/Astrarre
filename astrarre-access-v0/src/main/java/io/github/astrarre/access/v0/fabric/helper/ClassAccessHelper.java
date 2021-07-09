package io.github.astrarre.access.v0.fabric.helper;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.astrarre.access.v0.api.MapFilter;
import io.github.astrarre.access.v0.api.helper.AbstractAccessHelper;
import io.github.astrarre.util.v0.api.func.IterFunc;

public class ClassAccessHelper<T, F> extends AbstractAccessHelper<Class<? extends T>, F> {
	protected final MapFilter<Class<? extends T>, F> filterClassExact;
	protected final MapFilter<Class<? super T>, F> filterSuper, filterInterfaces;

	public ClassAccessHelper(AbstractAccessHelper<Class<? extends T>, F> copyFrom) {
		this(copyFrom.iterFunc, copyFrom.andThen, copyFrom.empty);
	}

	public ClassAccessHelper(IterFunc<F> func, Consumer<Function<Class<? extends T>, F>> adder, F empty) {
		super(func, adder, empty);
		this.filterClassExact = new MapFilter<>(func, empty);
		this.filterSuper = new MapFilter<>(func, empty);
		this.filterInterfaces = new MapFilter<>(func, empty);
	}

	public ClassAccessHelper<T, F> forClassExact(Class<? extends T> type, F func) {
		if(this.filterClassExact.add(type, func)) {
			this.andThen.accept(this.filterClassExact::get);
		}
		return this;
	}

	public ClassAccessHelper<T, F> isInstance(Class<? super T> type, F func) {
		if(type.isInterface()) {
			if(this.filterInterfaces.add(type, func)) {
				this.andThen.accept(this::getInterfaceFunction);
			}
		} else {
			if(this.filterSuper.add(type, func)) {
				this.andThen.accept(c -> {
					Class<? super T> current = (Class<? super T>) c;
					while(current != null) {
						F found = this.filterSuper.get(current);
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

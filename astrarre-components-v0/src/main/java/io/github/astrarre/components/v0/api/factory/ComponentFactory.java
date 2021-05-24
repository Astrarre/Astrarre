package io.github.astrarre.components.v0.api.factory;

import io.github.astrarre.components.internal.access.CopyAccess;
import io.github.astrarre.components.internal.lazyAsm.LazyAsmComponentFactory;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.util.v0.api.Id;

public abstract class ComponentFactory<C> {
	private final LazyAsmComponentFactory<C> impl;
	public ComponentFactory(Id name) {
		this.impl = new LazyAsmComponentFactory<C>(name.toString().replace(":", "_")) {
			@Override
			public int getVersion(C context) {
				return ComponentFactory.this.getVersion(context);
			}

			@Override
			public CopyAccess getData(C context) {
				return ComponentFactory.this.getData(context);
			}

			@Override
			public void setData(C context, CopyAccess data, int version) {
				ComponentFactory.this.setData(context, data, version);
			}
		};
	}

	public <V, T extends Component<C, V>> T create(Class<T> componentType, Id id) {
		return this.impl.create(componentType, id.toString().replace(":", "_"));
	}

	@SafeVarargs
	public final <V, T extends Component<C, V>> T createInfer(Id id, T... vargs) {
		return (T) this.impl.create((Class)vargs.getClass().getComponentType(), id.toString().replace(":", "_"));
	}

	protected abstract int getVersion(C context);

	protected abstract CopyAccess getData(C context);

	protected abstract void setData(C context, CopyAccess data, int version);
}

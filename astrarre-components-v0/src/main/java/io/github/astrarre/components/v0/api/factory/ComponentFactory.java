package io.github.astrarre.components.v0.api.factory;

import io.github.astrarre.components.internal.access.CopyAccess;
import io.github.astrarre.components.v0.api.components.Component;

public abstract class ComponentFactory<C> {
	public abstract <V, T extends Component<C, V>> T create(Class<T> componentType, String modid, String name);

	@SafeVarargs
	public final <V, T extends Component<C, V>> T createInfer(String modid, String name, T... vargs) {
		return (T) this.create((Class)vargs.getClass().getComponentType(), modid, name);
	}

	protected abstract int getVersion(C context);

	protected abstract CopyAccess getData(C context);

	protected abstract void setData(C context, CopyAccess data, int version);
}

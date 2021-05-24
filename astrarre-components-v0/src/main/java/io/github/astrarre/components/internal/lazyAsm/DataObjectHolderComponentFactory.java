package io.github.astrarre.components.internal.lazyAsm;

import io.github.astrarre.components.internal.access.CopyAccess;
import io.github.astrarre.components.internal.access.DataObjectHolder;
import io.github.astrarre.components.v0.api.factory.ComponentFactory;
import io.github.astrarre.util.v0.api.Id;

public class DataObjectHolderComponentFactory<C> extends ComponentFactory<C> {
	public DataObjectHolderComponentFactory(Id name) {
		super(name);
	}

	@Override
	protected int getVersion(C context) {
		return ((DataObjectHolder)context).astrarre_getVersion();
	}

	@Override
	protected CopyAccess getData(C context) {
		return ((DataObjectHolder)context).astrarre_getObject();
	}

	@Override
	protected void setData(C context, CopyAccess data, int version) {
		((DataObjectHolder)context).astrarre_setObject(data, version);
	}
}

package io.github.astrarre.components.internal.lazyAsm;

import io.github.astrarre.components.internal.access.CopyAccess;
import io.github.astrarre.components.internal.access.DataObjectHolder;

public class DataObjectHolderComponentFactory<C> extends AbstractComponentFactory<C> {
	public DataObjectHolderComponentFactory(String modid, String path) {
		super(modid, path);
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

package io.github.astrarre.components.v0.api.factory;

import io.github.astrarre.components.internal.lazyAsm.standard.CopyAccess;

public interface DataObjectHolder {
	CopyAccess astrarre_getObject();

	int astrarre_getVersion();

	void astrarre_setObject(CopyAccess object, int version);
}

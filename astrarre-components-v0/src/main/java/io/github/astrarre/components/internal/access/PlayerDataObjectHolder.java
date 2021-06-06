package io.github.astrarre.components.internal.access;

import io.github.astrarre.components.internal.lazyAsm.standard.CopyAccess;

public interface PlayerDataObjectHolder {
	CopyAccess astrarre_getObject_p();

	int astrarre_getVersion_p();

	void astrarre_setObject_p(CopyAccess object, int version);
}

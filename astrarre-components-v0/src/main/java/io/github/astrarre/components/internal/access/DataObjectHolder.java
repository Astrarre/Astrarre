package io.github.astrarre.components.internal.access;

public interface DataObjectHolder {
	CopyAccess astrarre_getObject();

	int astrarre_getVersion();

	void astrarre_setObject(CopyAccess object, int version);
}

package io.github.astrarre.components.internal.lazyAsm.standard;

public interface CopyAccess {
	void copyTo(Object object);

	CopyAccess cloneMe();
}

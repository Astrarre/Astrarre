package io.github.astrarre.components.internal.access;

public interface CopyAccess {
	void copyTo(Object object);

	CopyAccess cloneMe();
}

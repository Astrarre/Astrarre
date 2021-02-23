package io.github.astrarre.itemview.internal.access;

import io.github.astrarre.itemview.v0.api.nbt.NBTType;

public interface AbstractListTagAccess {
	Object itemview_getListTag(NBTType<?> type);
}

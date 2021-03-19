package io.github.astrarre.itemview.v0.api;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;

public interface Serializable {
	void save(NBTagView.Builder tag, String key);
}

package io.github.astrarre.gui.v1.api;

import io.github.astrarre.util.v0.api.Edge;

import net.minecraft.client.gui.ParentElement;

public interface Panel {

	boolean isInvalid();

	@Edge
	ParentElement asMinecraft();
}

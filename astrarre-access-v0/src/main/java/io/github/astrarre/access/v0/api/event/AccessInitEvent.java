package io.github.astrarre.access.v0.api.event;

import io.github.astrarre.access.v0.api.Access;

public interface AccessInitEvent {
	void onInit(Access<?> access);
}

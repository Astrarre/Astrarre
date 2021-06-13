package io.github.astrarre.event.v0.api.entity;

import io.github.astrarre.event.v0.api.entity.access.EntityBlockPermissionAccess;
import io.github.astrarre.util.v0.api.Id;

public interface EntityPermissions {
	EntityBlockPermissionAccess BREAK_BLOCK = new EntityBlockPermissionAccess(Id.create("astrarre-event-entity-v0", "break_block"));
	EntityBlockPermissionAccess PLACE_BLOCK = new EntityBlockPermissionAccess(Id.create("astrarre-event-entity-v0", "place_block"));
	EntityBlockPermissionAccess INTERACT_BLOCK = new EntityBlockPermissionAccess(Id.create("astrarre-event-entity-v0", "interact_block"));
}

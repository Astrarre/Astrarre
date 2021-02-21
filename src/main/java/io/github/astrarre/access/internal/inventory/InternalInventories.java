package io.github.astrarre.access.internal.inventory;

import io.github.astrarre.access.v0.api.WorldAccess;

import net.minecraft.inventory.Inventory;

public class InternalInventories {
	public static final WorldAccess<Inventory> INVENTORY_PROVIDER_REGISTRY = new WorldAccess<>();
	static {
		INVENTORY_PROVIDER_REGISTRY.addWorldProviderFunctions();
	}
}

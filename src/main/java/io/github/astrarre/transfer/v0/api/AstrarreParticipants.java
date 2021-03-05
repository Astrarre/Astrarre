package io.github.astrarre.transfer.v0.api;

import java.util.HashSet;
import java.util.Set;

import io.github.astrarre.access.v0.api.FunctionAccess;
import io.github.astrarre.access.v0.api.WorldAccess;
import io.github.astrarre.access.v0.api.func.WorldFunction;
import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.transfer.internal.astrarre.TransferInternalAstrarre;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.v0.fluid.Fluid;
import io.github.astrarre.v0.item.Item;
import org.jetbrains.annotations.NotNull;

import net.minecraft.block.InventoryProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;


public class AstrarreParticipants {
	/**
	 * if a participant is looking for a limited set of items, this can help narrow it down
	 */
	public static final FunctionAccess<Insertable<ItemKey>, @NotNull Set<Item>> FILTERS = FunctionAccess.newInstance(sets -> {
		Set<Item> combined = new HashSet<>();
		sets.forEach(combined::addAll);
		return combined;
	});

	public static final WorldAccess<Participant<ItemKey>> ITEM_WORLD = new WorldAccess<>(Participants.EMPTY.cast());
	public static final WorldAccess<Participant<Fluid>> FLUID_WORLD = new WorldAccess<>(Participants.EMPTY.cast());

	static {
		FILTERS.addProviderFunction();
		ITEM_WORLD.addWorldProviderFunctions();
		FLUID_WORLD.addWorldProviderFunctions();

		FILTERS.dependsOn(FabricParticipants.FILTERS, function -> insertable -> (Set) function.apply((Insertable)insertable));
		FabricParticipants.FILTERS.dependsOn(FILTERS, function -> insertable -> (Set) function.apply((Insertable)insertable));
	}

}

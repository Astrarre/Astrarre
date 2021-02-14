package io.github.astrarre.testmod;

import java.util.Objects;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.func.EntityFunction;
import io.github.astrarre.access.v0.api.func.WorldFunction;
import io.github.astrarre.itemview.v0.api.item.ItemKey;
import io.github.astrarre.transfer.internal.InventoryParticipants;
import io.github.astrarre.transfer.v0.api.participants.AggregateParticipant;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Items;
import net.minecraft.util.math.Matrix3f;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class TestModPreLaunchTesting implements PreLaunchEntrypoint {
	public static final Access<WorldFunction<Float>, Float> WORLD = new Access<>(WorldFunction::andThen, WorldFunction.empty());
	public static final Access<EntityFunction<Integer>, Integer> ACCESS = new Access<>(EntityFunction::andThen, EntityFunction.empty());

	static {
		WORLD.andThen((direction, state, view, pos, entity) -> 4.3f);

		WORLD.addDependencyType(ACCESS, function -> (direction, state, view, pos, entity) -> Float.valueOf(function.get(direction, null)));

		ACCESS.addDependencyType(WORLD,
				function -> (direction, entity) -> Objects.requireNonNull(function.get(direction, null, null, null, null)).intValue());
		//WORLD.addDependency(PROVIDER, EntityFunction.of(PROVIDER));
	}

	@Override
	public void onPreLaunch() {
		Matrix3f matrix = Matrix3f.scale(2, 2, 2);
		Vector3f vec = new Vector3f(2, 2, 2);
		vec.transform(matrix);
		vec.add(vec);
		System.out.println(vec);

		Inventory inventory = new SimpleInventory(27);
		AggregateParticipant<ItemKey> participant = InventoryParticipants.get(inventory);
		participant.insert(null, ItemKey.of(Items.STONE), 10);
		participant.insert(null, ItemKey.of(Items.IRON_INGOT), 10);
		participant.insert(null, ItemKey.of(Items.REDSTONE_BLOCK), 10);
		participant.insert(null, ItemKey.of(Items.TNT), 10);
		System.out.println(inventory);

		System.out.println(ACCESS.get().get(null, null));
		System.out.println(WORLD.get().get(null, null, null, null, null));
		System.exit(0);
	}
}

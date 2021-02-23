package io.github.astrarre.testmod;

import java.util.List;

import com.google.common.collect.Sets;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.transfer.internal.participantInventory.SetMatchingInsertable;
import io.github.astrarre.transfer.v0.api.AstrarreParticipants;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import it.unimi.dsi.fastutil.ints.IntList;

import net.minecraft.Bootstrap;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class TestModPreLaunchTesting implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		// test nbt api
		NBTagView tag = FabricViews.view(new CompoundTag());
		NBTType<List<List<IntList>>> nbt = NBTType.listOf(NBTType.listOf(NBTType.INT_ARRAY));
		List<List<IntList>> lis = tag.get("e", nbt);

		Bootstrap.initialize();
		// test api api
		SetMatchingInsertable insertable = new SetMatchingInsertable(Sets.newHashSet(Items.ACACIA_BOAT, Items.STONE), 10);
		System.out.println(AstrarreParticipants.FILTERS.get().apply((Insertable) insertable));
	}
}

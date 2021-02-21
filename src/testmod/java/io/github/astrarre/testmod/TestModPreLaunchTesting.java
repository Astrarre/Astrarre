package io.github.astrarre.testmod;

import java.util.List;

import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.astrarre.itemview.v0.api.item.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.item.nbt.NBTagView;
import it.unimi.dsi.fastutil.ints.IntList;

import net.minecraft.nbt.CompoundTag;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class TestModPreLaunchTesting implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		NBTagView tag = FabricViews.view(new CompoundTag());
		NBTType<List<List<IntList>>> nbt = NBTType.listOf(NBTType.listOf(NBTType.INT_ARRAY));
		List<List<IntList>> lis = tag.get("e", nbt);
	}
}

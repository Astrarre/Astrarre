package io.github.astrarre.testmod;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class TestModPreLaunchTesting implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		NBTagView builder = NBTagView.builder()
				.set("hello", 0)
				.build();
		System.out.println(builder);
		System.exit(0);
	}
}

package io.github.astrarre.testmod;

import io.github.astrarre.v0.util.math.Direction;

import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {
	@Override
	public void onInitialize() {
		System.out.println(Direction.NORTH);
		System.exit(0);
	}
}

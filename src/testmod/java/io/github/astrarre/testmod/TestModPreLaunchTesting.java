package io.github.astrarre.testmod;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Matrix3f;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class TestModPreLaunchTesting implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		Matrix3f matrix = Matrix3f.scale(2, 2, 2);
		Vector3f vec = new Vector3f(2, 2, 2);
		vec.transform(matrix);
		vec.add(vec);
		System.out.println(vec);
		//System.exit(0);
	}
}

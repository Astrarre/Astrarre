import java.io.IOException;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.AbstracterUtil;
import io.github.astrarre.abstracter.abs.InterfaceAbstracter;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Main {
	public static void main(String[] args) throws IOException {
		System.out.println("astrarre-common-v0 abstractions!");
		AbstracterUtil.applyParallel(args[0], () -> {
			AbstracterConfig.registerInterface(new InterfaceAbstracter(Identifier.class)).name("io/github/astrarre/v0/util/Id");
			AbstracterUtil.registerDefaultInterface(BlockPos.class, World.class, MinecraftServer.class);
			AbstracterConfig.registerInterface(new InterfaceAbstracter(MinecraftServer.class).);
		});
		System.out.println("Done!");
	}
}

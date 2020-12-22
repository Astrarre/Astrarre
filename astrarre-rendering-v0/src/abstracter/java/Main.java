import java.io.IOException;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.AbstracterUtil;
import io.github.astrarre.abstracter.abs.InterfaceAbstracter;

import net.minecraft.network.PacketByteBuf;

public class Main {
	public static void main(String[] args) throws IOException {
		System.out.println("astrarre-rendering-v0 abstractions!");
		AbstracterUtil.applyParallel(args[0], () -> {
			AbstracterConfig.registerInterface(new InterfaceAbstracter(PacketByteBuf.class));
		});
		System.out.println("Done!");
	}
}

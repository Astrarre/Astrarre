import java.io.IOException;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.AbstracterUtil;
import io.github.astrarre.abstracter.abs.InterfaceAbstracter;

import net.minecraft.util.Identifier;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		AbstracterUtil util = AbstracterUtil.fromFile(args[0]);
		AbstracterConfig config = util.createConfig("astrarre_manifest");
		config.registerInterface(new InterfaceAbstracter(Identifier.class)).name("io/github/astrarre/v0/util/Id");
		util.write(config);
	}
}

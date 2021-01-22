import java.io.IOException;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.AbstracterUtil;

import net.minecraft.client.texture.Sprite;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		AbstracterUtil util = AbstracterUtil.fromFile(args[0]);
		AbstracterConfig config = util.createConfig("astrarre_manifest");
		config.registerInterface(Sprite.class);
		util.write(config);
	}
}
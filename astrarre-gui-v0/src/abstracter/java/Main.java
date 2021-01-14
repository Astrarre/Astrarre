import java.io.IOException;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.AbstracterUtil;

import net.minecraft.client.texture.Sprite;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		AbstracterConfig config = new AbstracterConfig();
		AbstracterUtil.applyParallel(config, args[0], () -> config.registerInterface(Sprite.class));
	}
}
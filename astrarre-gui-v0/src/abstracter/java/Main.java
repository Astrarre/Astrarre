import java.io.IOException;

import io.github.astrarre.abstracter.AbstracterUtil;

import net.minecraft.client.texture.Sprite;

public class Main {
	public static void main(String[] args) throws IOException {
		AbstracterUtil.applyParallel(args[0], () -> AbstracterUtil.registerDefaultInterface(Sprite.class));
	}
}
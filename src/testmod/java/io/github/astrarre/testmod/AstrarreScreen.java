package io.github.astrarre.testmod;

import io.github.astrarre.rendering.v0.fabric.MatrixGraphics;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Close;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AstrarreScreen extends Screen {
	private static final float SIZE = 40;
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/beacon.png");
	int tick = 0;

	protected AstrarreScreen(Text title) {
		super(title);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		try {
			MatrixGraphics impl = new MatrixGraphics(matrices);
			Close closeable = impl.applyTransformation(new Transformation(0, 0, 45, 10, 10, 0, 1, 1, 1));
			impl.fillRect(10, 10, 0xffaaffaa);
			closeable.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void tick() {
		super.tick();
		this.tick++;
	}
}

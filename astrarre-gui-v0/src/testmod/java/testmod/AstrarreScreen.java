package testmod;

import io.github.astrarre.gui.internal.Graphics3DImpl;
import io.github.astrarre.gui.v0.api.Transformation;
import io.github.astrarre.gui.v0.api.util.Closeable;
import io.github.astrarre.v0.util.math.Vec3f;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class AstrarreScreen extends Screen {
	protected AstrarreScreen(Text title) {
		super(title);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		try {
			Graphics3DImpl g3d = new Graphics3DImpl(matrices);
			Transformation transformation = new Transformation(Vec3f.newInstance(45, 45, 45), Vec3f.newInstance(10, 10, 10), Vec3f.newInstance(1, 1, 1));
			try (Closeable closeable = g3d.applyTransformation(transformation)) {
				g3d.fillRect(10, 10, 0xffffaaff);
			}
		} catch (Throwable e) {
			System.out.println(e.getMessage());
		}
	}
}

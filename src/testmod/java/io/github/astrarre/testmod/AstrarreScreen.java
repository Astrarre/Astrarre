package io.github.astrarre.testmod;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ContainerAccess;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.drawable.Button;
import io.github.astrarre.rendering.internal.util.MatrixGraphicsUtil;
import io.github.astrarre.rendering.v0.api.Transformation;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.text.Text;

public class AstrarreScreen extends Screen implements Element {
	private List<Vector3d> e = new ArrayList<>();

	public AstrarreScreen(Text title) {
		super(title);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		for (Drawable drawable : ((ContainerAccess) this).getContainer().getContentPanel()) {
			drawable.getBounds().walk((x1, y1, z1, x2, y2, z2) -> {
				MatrixGraphicsUtil.line(matrices, x1, x2, y1, y2, 0xffffaaff);
			});
		}
		for (Vector3d d : e) {
			MatrixGraphicsUtil.fill(matrices.peek().getModel(), (float)d.x, (float)d.y, (float)d.x+1, (float)d.y+1, 0xffaaffaa);
		}
	}

	@Override
	protected void init() {
		RootContainerInternal internal = ((ContainerAccess) this).getContainer();
		Button button = new Button(internal);
		internal.getContentPanel().addClient(button);
		button.setTransformation(Transformation.translate(10, 10, 0).combine(Transformation.rotate(0, 0, 30)));
	}

	// todo ticking components
	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		boolean val = super.mouseReleased(mouseX, mouseY, button);
		System.out.println(val);
		if (val) {
			this.e.add(new Vector3d(mouseX, mouseY, 0));
		}
		return val;
	}
}

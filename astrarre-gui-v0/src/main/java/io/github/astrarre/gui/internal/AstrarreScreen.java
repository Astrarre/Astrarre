package io.github.astrarre.gui.internal;

import io.github.astrarre.gui.v0.api.Graphics3d;
import io.github.astrarre.gui.v0.api.components.DynamicBound;
import io.github.astrarre.gui.v0.api.util.Rect4f;
import io.github.astrarre.gui.v0.api.components.button.ButtonTextures;
import io.github.astrarre.gui.v0.api.util.Closeable;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;


public class AstrarreScreen extends Screen implements Element {
	static {
		//ScreenRegistry.register(GuiScreenHandler.TYPE, GuiScreen::new);
	}

	ClientButton button = new ClientButton(ButtonTextures.BEACON_BUTTON);

	public AstrarreScreen(Text title) {
		super(title);
	}

	@Override
	protected void init() {
		super.init();
		this.button.bounds = DynamicBound.cartesian(-9, -9, 18, 18);
		this.button.registerAction(() -> {
			System.out.println("-----------------------------------------");
			System.out.println("Action!");
			System.out.println("-----------------------------------------");
		});
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		Graphics3d graphics3D = new Graphics3DImpl(matrices);
		Rect4f rect4F = this.button.bounds.getLocation(this.width, this.height);
		try(Closeable c = graphics3D.translate(rect4F.x, rect4F.y, getZOffset())) {
			this.button.render(graphics3D, delta);
		}
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(this.button.isIn(this.width, this.height, (float) mouseX, (float) mouseY)) {
			this.button.onPress(null, button);
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if(this.button.isIn(this.width, this.height, (float) mouseX, (float) mouseY)) {
			this.button.onRelease(null, button);
			return true;
		}
		return false;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		if(this.button.isIn(this.width, this.height, (float) mouseX, (float) mouseY)) {
			this.button.onMouseOver((float) mouseX, (float) mouseY);
		} else {
			this.button.onMouseOutside();
		}
	}
}

package io.github.astrarre.testmod;

import java.util.Collections;

import io.github.astrarre.gui.internal.ContainerInternal;
import io.github.astrarre.gui.v0.api.bounds.Interactable;
import io.github.astrarre.gui.v0.api.drawable.Button;
import io.github.astrarre.networking.v0.api.network.NetworkMember;
import io.github.astrarre.rendering.internal.MatrixGraphics;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Close;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class AstrarreScreen extends Screen implements Element {
	private final Button button;
	int tick = 0;

	public AstrarreScreen(Text title) {
		super(title);
		this.button = new Button(new ClientAstrarreContainer());
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);

		try {
			MatrixGraphics impl = new MatrixGraphics(matrices);
			this.button.render(impl, delta);

			Close closeable = impl.applyTransformation(new Transformation(0, 0, 45, 10, 10, 0, 1, 1, 1));

			impl.fillRect(10, 10, 0xffaaffaa);

			closeable.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.button.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void tick() {
		super.tick();
		this.tick++;
	}

	@Override
	@Environment (EnvType.CLIENT)
	public void mouseMoved(double mouseX, double mouseY) {
		this.button.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.button.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return this.button.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return this.button.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return this.button.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return this.button.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean charTyped(char chr, int modifiers) {
		return this.button.charTyped(chr, modifiers);
	}

	@Override
	@Environment (EnvType.CLIENT)
	public boolean changeFocus(boolean lookForwards) {
		return this.button.changeFocus(lookForwards);
	}

	public static class ClientAstrarreContainer extends ContainerInternal {
		@Override
		public Type getType() {
			return Type.SCREEN;
		}

		@Override
		public boolean isClient() {
			return true;
		}

		@Override
		public Iterable<NetworkMember> getViewers() {
			return Collections.emptyList();
		}

		@Override
		public boolean setFocus(Interactable drawable) {
			return false;
		}

		@Override
		public boolean isDragging() {
			return false;
		}
	}
}

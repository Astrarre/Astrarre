package io.github.astrarre.testmod;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.common.v0.api.util.math.Transformation;
import io.github.astrarre.gui.internal.DrawableHelper2;
import io.github.astrarre.gui.internal.Graphics3DImpl;
import io.github.astrarre.gui.v0.api.util.Closeable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

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
			Graphics3DImpl impl = new Graphics3DImpl(matrices);
			Closeable closeable = impl.applyTransformation(new Transformation(0, 0, 45, ));

			impl.fillRect(10, 10, 0xffaaffaa);

			closeable.close();

			Matrix4f matrix = matrices.peek().getModel();
			Tessellator tessellator = Tessellator.getInstance();
			for (Vector3f[] quad : DrawableHelper2.iterateAsQuads(new Vector3f[] {
					new Vector3f(50, 0, 2),
					new Vector3f(25, 10, 2),
					new Vector3f(30, 40, 2),
					new Vector3f(70, 40, 2),
					new Vector3f(75, 10, 2)
			})) {
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				RenderSystem.disableTexture();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
				bufferBuilder.vertex(matrix, quad[0].getX(), quad[0].getY(), quad[0].getZ()).color(1.0f, 1.0f, 1.0f, 1.0f).next();
				bufferBuilder.vertex(matrix, quad[1].getX(), quad[1].getY(), quad[1].getZ()).color(1.0f, 1.0f, 1.0f, 1.0f).next();
				bufferBuilder.vertex(matrix, quad[2].getX(), quad[2].getY(), quad[2].getZ()).color(1.0f, 1.0f, 1.0f, 1.0f).next();
				bufferBuilder.vertex(matrix, quad[3].getX(), quad[3].getY(), quad[3].getZ()).color(1.0f, 1.0f, 1.0f, 1.0f).next();
				bufferBuilder.end();
				BufferRenderer.draw(bufferBuilder);
				RenderSystem.disableBlend();
				RenderSystem.enableTexture();
			}
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

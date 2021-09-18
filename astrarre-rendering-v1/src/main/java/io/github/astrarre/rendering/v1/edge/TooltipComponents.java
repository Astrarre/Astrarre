package io.github.astrarre.rendering.v1.edge;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.internal.Renderer2DImpl;
import io.github.astrarre.rendering.internal.Renderer3DImpl;
import io.github.astrarre.rendering.v1.api.space.Render3d;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class TooltipComponents {
	public static TooltipComponent from(Icon icon) {
		return new TooltipComponent() {
			@Override
			public int getHeight() {
				return (int) icon.height();
			}

			@Override
			public int getWidth(TextRenderer textRenderer) {
				return (int) icon.width();
			}

			@Override
			public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate) {
				MatrixStack stack = new MatrixStack();
				stack.method_34425(matrix4f);
				stack.translate(x, y, 0);

				MinecraftClient client = MinecraftClient.getInstance(); // todo change the API to allow it to use VertexConsumerProviders instead
				Renderer3DImpl impl = new Renderer3DImpl(textRenderer, stack, Tessellator.getInstance().getBuffer(), client.getItemRenderer(),
				                                         this.getWidth(null),
				                                         this.getHeight());
				try {
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					RenderSystem.enableDepthTest();
					icon.render(impl);
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					impl.flush();
				}
			}
		};
	}

	public static Icon from(TooltipComponent component) {
		return new Icon() {
			@Override
			public float height() {
				return component.getHeight();
			}

			@Override
			public float width() {
				return component.getWidth(MinecraftClient.getInstance().textRenderer);
			}

			@Override
			public void render(Render3d render) {
				if(render instanceof Renderer2DImpl r) {
					TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
					component.drawText(renderer, 0, 0, r.stack.peek().getModel(), VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer()));
				} else {
					throw new UnsupportedOperationException("unsupported renderer class " + render);
				}
			}
		};
	}
}

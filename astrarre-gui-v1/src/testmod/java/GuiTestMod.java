import com.google.gson.Gson;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.rendering.internal.Renderer3DImpl;
import io.github.astrarre.rendering.v1.api.space.Render3D;
import io.github.astrarre.rendering.v1.edge.OpenGLRenderer;
import io.github.astrarre.rendering.v1.edge.vertex.VertexFormat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public class GuiTestMod implements ModInitializer {
	final Item h = new Item(new Item.Settings()) {
		@Override
		public ActionResult useOnBlock(ItemUsageContext context) {
			if(context.getWorld().isClient) {
				MinecraftClient client = MinecraftClient.getInstance();
				client.openScreen(new TestScreen());
			}
			return super.useOnBlock(context);
		}
	};

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("mymod:y"), h);
	}

	private static class TestScreen extends Screen {
		public TestScreen() {super(new LiteralText("urmom"));}

		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			super.render(matrices, mouseX, mouseY, delta);
			Render3D render = new Renderer3DImpl(this.textRenderer, matrices, Tessellator.getInstance().getBuffer());
			try {
				extracted(render);
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				render.flush();
			}
		}
	}
	// start = pos, end = end
	private static void extracted(Render3D render) {
		OpenGLRenderer renderer = render.ogl();
		var icon = renderer.render(VertexFormat.POS_TEX).texture(DrawableHelper.GUI_ICONS_TEXTURE);
		var stats = renderer.render(VertexFormat.POS_TEX).texture(DrawableHelper.STATS_ICON_TEXTURE);
		icon.quad().pos(0, 0, 0).tex(0, 0);
		icon.quad().pos(0, 25, 0).tex(0, 1); // 9th starting from 1
		icon.quad().pos(25, 25, 0).tex(1, 1); // 10th
		icon.quad().pos(25, 0, 0).tex(1, 0); // 9th

		stats.quad().pos(25, 0, 0).tex(0, 0);
		stats.quad().pos(25, 25, 0).tex(0, 1);
		stats.quad().pos(50, 25, 0).tex(1, 1);
		stats.quad().pos(50, 0, 0).tex(1, 0);

		icon.quad().pos(50, 0, 0).tex(0, 0);
		icon.quad().pos(50, 25, 0).tex(0, 1);
		icon.quad().pos(75, 25, 0).tex(1, 1);
		icon.quad().pos(75, 0, 0).tex(1, 0);
	}
}

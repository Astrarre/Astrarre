import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.rendering.internal.Renderer3DImpl;
import io.github.astrarre.rendering.v1.api.util.AngleFormat;
import io.github.astrarre.util.v0.api.SafeCloseable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
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
				client.openScreen(new Screen(new LiteralText("urmom")) {

					@Override
					public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
						super.render(matrices, mouseX, mouseY, delta);
						Renderer3DImpl impl = new Renderer3DImpl(this.textRenderer, matrices, Tessellator.getInstance().getBuffer());
						try {
							//RenderSystem.lineWidth(10);

							/*try(SafeCloseable ignore = impl.scale(2, .5f, 1)) {
								impl.fill().rect(0xFFaaffaa, 10, 10, 100, 100);
								impl.outline().rect(0xFFFaaFFF, 5, 5, 105, 105);
							}*/

							//impl.line(0xFFFFaaFF, 0, 0, 10, 100, 100, 10);

							impl.fill().rect(0xffaaaaaa, 20, 8, 120, 13);
							impl.text(0xffffffff, 30, 10, true).renderScrollingText(new LiteralText("bruh_moment"), (System.currentTimeMillis() % 10_000) / 100f, 100, false);
						} finally {
							impl.flush();
						}
					}
				});
			}
			return super.useOnBlock(context);
		}
	};

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("mymod:y"), h);
	}
}

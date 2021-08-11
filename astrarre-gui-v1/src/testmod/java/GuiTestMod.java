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
						Renderer3DImpl impl = new Renderer3DImpl(matrices, Tessellator.getInstance().getBuffer());
						try {
							//impl.line(0xFFFFaaFF, 0, 0, 0, 100);
							try(SafeCloseable ignore = impl.scale(2, .5f, 1)) {
								impl.fill().rect(0xFFaaffaa, 10, 10, 100, 100);
								impl.outline().rect(0xFFFFFFFF, 5, 5, 105, 105);
							}

							/*try(SafeCloseable ignore1 = impl.translate(30, 30, 30)) {
								try(SafeCloseable ignore = impl.rotate(AngleFormat.DEGREES, System.currentTimeMillis() / 100f)) {
									impl.outline().rect(0xFFFFFFFF, 5, 5, 105, 105);
								}
							}*/
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

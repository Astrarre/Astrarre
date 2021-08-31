import io.github.astrarre.gui.ElementRootPanel;
import io.github.astrarre.gui.v1.api.AComponent;
import io.github.astrarre.gui.v1.api.component.AButton;
import io.github.astrarre.gui.v1.api.component.icon.Icon;
import io.github.astrarre.gui.v1.api.component.icon.Icons;
import io.github.astrarre.gui.v1.api.component.icon.ScrollingLabelIcon;
import io.github.astrarre.gui.v1.api.cursor.Cursor;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import io.github.astrarre.rendering.v1.api.util.AngleFormat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
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
		final ElementRootPanel panel = new ElementRootPanel.ScreenImpl(this);

		public TestScreen() {
			super(new LiteralText("urmom"));
			this.panel.add(AButton.icon(Icons.Groups.X).callback(() -> System.out.println("hello!")).tooltip((cursor, render) -> {
				var builder = render.tooltip();
				builder.textRenderer(0xffffffff, true).render("this is a button");
				builder.add(Icons.FURNACE_FLAME_ON); // todo add a current width to the tooltip builder
				builder.add(Icon.scrollingText(new LiteralText("This is a very long sentence, too long to fit"), 80));
				builder.render();
			}).addTransform(Transform3d.translate(30, 30, 0)));
		}

		@Override
		protected void init() {
			super.init();
			this.addDrawableChild(this.panel);
		}
	}
}

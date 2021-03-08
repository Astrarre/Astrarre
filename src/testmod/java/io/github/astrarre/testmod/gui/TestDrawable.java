package io.github.astrarre.testmod.gui;

import java.util.List;

import com.google.common.collect.ImmutableList;
import io.github.astrarre.gui.v0.api.AstrarreIcons;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.serializer.ToPacketSerializer;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.rendering.v0.api.Transformation;
import io.github.astrarre.rendering.v0.api.util.Close;
import io.github.astrarre.rendering.v0.api.util.Polygon;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class TestDrawable extends Drawable implements Interactable {
	// use a translateable text when possible I'm just lazy
	private static final List<Text> TEXT = ImmutableList.of(new LiteralText("The amount of power contained in this item"));
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerNoInput(Id.create("mymod", "drawable"), TestDrawable::new);
	private static final Transformation TRANSLATE = Transformation.translate(7, 0, 0);

	public final SyncedProperty<Integer> power = this.createClientSyncedProperty(ToPacketSerializer.INTEGER, "power", 0);
	@Environment (EnvType.CLIENT) protected boolean isHover;

	public TestDrawable(RootContainer rootContainer) {
		super(rootContainer, ENTRY);
		this.setBounds(Polygon.rectangle(47, 7)); // make longer and thinner to align with icon
	}

	/**
	 * this must be called in your mod initializer
	 */
	public static void init() {}

	@Override
	protected void render0(Graphics3d graphics, float tickDelta) {
		graphics.drawTexture(AstrarreIcons.INFO);
		try (Close close = graphics.applyTransformation(TRANSLATE)) { // shift the power bar 9 pixels to the right to leave space for the icon
			// this is where you render your component
			// first we draw a background
			graphics.fillRect(40, 7, Graphics3d.getARGB(255, 255, 255));
			graphics.drawLine(0, 0, 0, 7, 0xff000000);
			// then we fill a bar up to whatever our power level is
			graphics.fillGradient(Math.min(this.power.get(), 40), 7, 0xff00ffaa, 0xffaaff00);
		}

		if (this.isHover) {
			graphics.drawTooltip(TEXT);
			graphics.fillGradient(7, 7, 0x80ffffff, 0x80ffffff);
			this.isHover = false;
		}
	}

	@Override
	protected void write0(Output output) {}

	@Override
	public boolean mouseHover(double mouseX, double mouseY) {
		if (mouseX <= 7) { // within the icon space
			this.isHover = true;
		}
		return false;
	}
}

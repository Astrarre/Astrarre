package io.github.astrarre.gui.v0.api.base.widgets;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.util.v0.api.Id;
import org.jetbrains.annotations.Nullable;

import net.minecraft.text.Text;

public class ALabel extends ADrawable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(Id.create("astrarre-gui-v0", "alabel"), ALabel::new);

	public final Text label;
	public final int color;
	public final boolean shadow;

	/**
	 * @param label the text to display
	 * @param color the color of the text
	 * @param shadow true if the shadow of the text should be rendered
	 */
	public ALabel(Text label, int color, boolean shadow) {
		this(ENTRY, label, color, shadow);
	}

	protected ALabel(DrawableRegistry.@Nullable Entry id, Text text, int color, boolean shadow) {
		super(id);
		this.label = text;
		this.color = color;
		this.shadow = shadow;
	}

	public ALabel(DrawableRegistry.Entry entry, NBTagView view) {
		super(entry);
		this.label = FabricSerializers.TEXT.read(view.getValue("text"));
		this.color = view.getInt("color");
		this.shadow = view.getBool("shadow");
	}

	@Override
	protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
		graphics.drawText(this.label, this.color, this.shadow);
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		output.putValue("text", FabricSerializers.TEXT.save(this.label));
		output.putInt("color", this.color);
		output.putBool("shadow", this.shadow);
	}

	public static void init() {}
}

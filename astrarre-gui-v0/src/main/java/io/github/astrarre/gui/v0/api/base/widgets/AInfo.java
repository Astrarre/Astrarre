package io.github.astrarre.gui.v0.api.base.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.astrarre.gui.v0.api.ADelegateDrawable;
import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.rendering.v0.api.Graphics2d;
import io.github.astrarre.gui.v0.api.graphics.GuiGraphics;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class AInfo extends ADelegateDrawable implements Interactable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(Id.create("astrarre-gui-v0", "info_widget"), AInfo::new);
	public final List<Text> tooltip;
	public final int width;
	@Environment (EnvType.CLIENT) protected boolean isHover;
	@Environment(EnvType.CLIENT) protected List<OrderedText> wrapped;

	/**
	 * @param width the maximum width of the tooltip (used for wrapping)
	 */
	public AInfo(ADrawable delegate, List<Text> tooltip, int width) {
		this(ENTRY, delegate, tooltip, width);
	}

	protected AInfo(DrawableRegistry.Entry id, ADrawable delegate, List<Text> tooltip, int width) {
		super(id, delegate);
		this.tooltip = Collections.unmodifiableList(tooltip);
		this.width = width;
	}

	protected AInfo(DrawableRegistry.Entry id, NBTagView input) {
		super(id, input);
		List<String> texts = input.get("tooltip", NBTType.listOf(NBTType.STRING));
		List<Text> list = new ArrayList<>(texts.size());
		for (String text : texts) {
			list.add(Text.Serializer.fromJson(text));
		}
		this.tooltip = Collections.unmodifiableList(list);
		this.width = input.getInt("width");

		this.wrapped = new ArrayList<>();
		for (Text text : list) {
			this.wrapped.addAll(Graphics2d.wrap(text, this.width));
		}
	}

	public static void init() {
	}

	@Override
	protected void render0(RootContainer container, GuiGraphics graphics, float tickDelta) {
		super.render0(container, graphics, tickDelta);
		if (this.isHover) {
			graphics.drawOrderedTooltip(this.wrapped);
		}
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		super.write0(container, output);
		List<String> texts = new ArrayList<>(this.tooltip.size());
		for (Text text : this.tooltip) {
			texts.add(Text.Serializer.toJson(text));
		}
		output.put("tooltip", NBTType.listOf(NBTType.STRING), texts);
		output.putInt("width", this.width);
	}

	@Override
	public boolean isHovering(RootContainer container, double mouseX, double mouseY) {
		return true;
	}

	@Override
	public void mouseHover(RootContainer container, double mouseX, double mouseY) {
		this.isHover = true;
	}

	@Override
	public void onLoseHover(RootContainer container) {
		this.isHover = false;
	}
}

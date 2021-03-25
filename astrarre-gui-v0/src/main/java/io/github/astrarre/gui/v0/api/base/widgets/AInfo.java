package io.github.astrarre.gui.v0.api.base.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.astrarre.gui.v0.api.ADrawable;
import io.github.astrarre.gui.v0.api.AstrarreIcons;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class AInfo extends ADrawable implements Interactable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(Id.create("astrarre-gui-v0", "info_widget"), AInfo::new);
	public final SyncedProperty<Boolean> isEnabled = this.createClientSyncedProperty(NBTType.BOOL, true);
	public final List<Text> tooltip;

	@Environment(EnvType.CLIENT)
	protected boolean isHover;
	protected AInfo(DrawableRegistry.Entry id, List<Text> tooltip) {
		super(id);
		this.tooltip = Collections.unmodifiableList(tooltip);
	}

	public AInfo(List<Text> tooltip) {
		this(ENTRY, tooltip);
	}

	protected AInfo(DrawableRegistry.Entry id, NBTagView input) {
		super(id);
		List<String> texts = input.get("tooltip", NBTType.listOf(NBTType.STRING));
		List<Text> list = new ArrayList<>(texts.size());
		for (String text : texts) {
			list.add(Text.Serializer.fromJson(text));
		}
		this.tooltip = Collections.unmodifiableList(list);
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		if(this.isEnabled.get()) {
			graphics.drawSprite(AstrarreIcons.INFO);
			if(this.isHover) {
				graphics.drawTooltip(this.tooltip);
				graphics.fillGradient(7, 7, 0x80ffffff, 0x80ffffff);
			}
		} else {
			graphics.drawSprite(AstrarreIcons.INFO_DARK);
		}
	}

	@Override
	protected void write0(RootContainer container, NBTagView.Builder output) {
		List<String> texts = new ArrayList<>(this.tooltip.size());
		for (Text text : this.tooltip) {
			texts.add(Text.Serializer.toJson(text));
		}
		output.put("tooltip", NBTType.listOf(NBTType.STRING), texts);
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

	public static void init() {
	}
}

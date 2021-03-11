package io.github.astrarre.gui.v0.api.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.astrarre.gui.v0.api.AstrarreIcons;
import io.github.astrarre.gui.v0.api.Drawable;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.access.Interactable;
import io.github.astrarre.networking.v0.api.SyncedProperty;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;
import io.github.astrarre.networking.v0.api.serializer.ToPacketSerializer;
import io.github.astrarre.networking.v0.fabric.FabricData;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.util.v0.api.Id;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class InfoWidget extends Drawable implements Interactable {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.registerForward(Id.create("astrarre-gui-v0", "info_widget"), InfoWidget::new);
	public final SyncedProperty<Boolean> isEnabled = this.createClientSyncedProperty(ToPacketSerializer.BOOLEAN, "enabled", true);
	public final List<Text> tooltip;

	@Environment(EnvType.CLIENT)
	protected boolean isHover;
	protected InfoWidget(DrawableRegistry.Entry id, List<Text> tooltip) {
		super(id);
		this.tooltip = Collections.unmodifiableList(tooltip);
	}

	public InfoWidget(List<Text> tooltip) {
		this(ENTRY, tooltip);
	}

	protected InfoWidget(DrawableRegistry.Entry id, Input input) {
		super(id);
		int count = input.readInt();
		List<Text> list = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			list.add(FabricData.read(input, PacketByteBuf::readText));
		}
		this.tooltip = Collections.unmodifiableList(list);
	}

	@Override
	protected void render0(RootContainer container, Graphics3d graphics, float tickDelta) {
		if(this.isEnabled.get()) {
			graphics.drawTexture(AstrarreIcons.INFO);
			if(this.isHover) {
				graphics.drawTooltip(this.tooltip);
				graphics.fillGradient(7, 7, 0x80ffffff, 0x80ffffff);
			}
		} else {
			graphics.drawTexture(AstrarreIcons.INFO_DARK);
		}
	}

	@Override
	protected void write0(RootContainer container, Output output) {
		output.writeInt(this.tooltip.size());
		for (Text text : this.tooltip) {
			FabricData.from(output).writeText(text);
		}
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

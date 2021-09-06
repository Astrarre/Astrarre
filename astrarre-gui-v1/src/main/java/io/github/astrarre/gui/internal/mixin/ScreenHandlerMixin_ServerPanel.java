package io.github.astrarre.gui.internal.mixin;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.gui.internal.access.TickingPanel;
import io.github.astrarre.gui.v1.api.server.ServerPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin_ServerPanel implements ServerPanel, TickingPanel {
	final List<Runnable> tick = new ArrayList<>(), close = new ArrayList<>();

	@Override
	public ScreenHandler screenHandler() {
		return (ScreenHandler) (Object) this;
	}

	@Override
	public ServerPanel addTickListener(Runnable runnable) {
		this.tick.add(runnable);
		return this;
	}

	@Override
	public ServerPanel addCloseListener(Runnable runnable) {
		this.close.add(runnable);
		return this;
	}

	@Inject(method = "close", at = @At("HEAD"))
	public void onClose(PlayerEntity player, CallbackInfo ci) {
		for(Runnable runnable : this.close) {
			runnable.run();
		}
	}

	@Override
	public void astrarre_tick() {
		for(Runnable runnable : this.tick) {
			runnable.run();
		}
	}
}

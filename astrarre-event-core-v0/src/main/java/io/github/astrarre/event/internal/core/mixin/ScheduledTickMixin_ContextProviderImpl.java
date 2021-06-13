package io.github.astrarre.event.internal.core.mixin;

import io.github.astrarre.event.internal.core.access.ContextProvider;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.ScheduledTick;

@Mixin(ScheduledTick.class)
public class ScheduledTickMixin_ContextProviderImpl implements ContextProvider {
	private Object astrarre_eventData;

	@Override
	public Object get() {
		return this.astrarre_eventData;
	}

	@Override
	public void set(Object val) {
		this.astrarre_eventData = val;
	}
}

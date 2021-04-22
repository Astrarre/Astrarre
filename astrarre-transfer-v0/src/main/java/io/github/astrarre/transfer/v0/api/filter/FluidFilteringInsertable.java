package io.github.astrarre.transfer.v0.api.filter;

import java.util.Collections;
import java.util.Set;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.Fluid;

public class FluidFilteringInsertable  extends FilteringInsertable<Fluid> implements Provider {
	public final Set<Fluid> items;
	public FluidFilteringInsertable(Set<Fluid> items, Insertable<Fluid> delegate) {
		super((object, quantity) -> items.contains(object), delegate);
		this.items = Collections.unmodifiableSet(items);
	}

	@Override
	public @Nullable Object get(Access<?> access) {
		return access == FabricParticipants.FLUID_FILTERS ? this.items : null;
	}
}

package io.github.astrarre.transfer.v0.api.filter;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.util.v0.fabric.Tags;
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;

/**
 * a filtering insertable based on a fluid tag
 */
public class FluidTagFilteringInsertable extends FilteringInsertable<Fluid> implements Provider {
	public final Tag<Fluid> tag;
	public FluidTagFilteringInsertable(Tag<Fluid> valid, Insertable<Fluid> delegate) {
		super((object, quantity) -> valid.contains(object), delegate);
		this.tag = valid;
	}

	@Override
	public @Nullable Object get(Access<?> access) {
		return access == FabricParticipants.FLUID_FILTERS ? Tags.get(this.tag) : null;
	}
}

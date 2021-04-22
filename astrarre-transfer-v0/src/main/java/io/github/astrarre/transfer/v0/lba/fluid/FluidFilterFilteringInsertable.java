package io.github.astrarre.transfer.v0.lba.fluid;

import java.util.Collections;
import java.util.Set;

import alexiil.mc.lib.attributes.fluid.filter.ExactFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidSetFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidTagFilter;
import alexiil.mc.lib.attributes.fluid.filter.RawFluidTagFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import com.google.common.collect.Sets;
import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.transfer.v0.api.Insertable;
import io.github.astrarre.transfer.v0.api.filter.FilteringInsertable;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import io.github.astrarre.util.v0.fabric.Tags;
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.Fluid;

public class FluidFilterFilteringInsertable extends FilteringInsertable<Fluid> implements Provider {
	public final FluidFilter filter;
	public final Set<Fluid> fluids;
	public final Insertable<Fluid> delegate;

	public FluidFilterFilteringInsertable(FluidFilter filter, Insertable<Fluid> delegate) {
		super((object, quantity) -> filter.matches(FluidKeys.get(object)), delegate);
		this.filter = filter;
		this.delegate = delegate;
		if(filter instanceof ExactFluidFilter) {
			FluidKey key = ((ExactFluidFilter) filter).fluid;
			if(key.getRawFluid() == null) {
				this.fluids = null;
			} else {
				this.fluids = Collections.singleton(key.getRawFluid());
			}
		} else if(filter instanceof FluidSetFilter) {
			this.fluids = (Set) Sets.filter(((FluidSetFilter) filter).getFluids(), input -> input.getRawFluid() == null);
		} else if(filter instanceof RawFluidTagFilter) {
			this.fluids = Tags.get(((RawFluidTagFilter) filter).tag);
		}  else if(filter instanceof FluidTagFilter) {
			//this.fluids = Tags.get(((FluidTagFilter) filter).res);
			this.fluids = null;
		} else {
			this.fluids = null;
		}
	}

	@Override
	public @Nullable Object get(Access<?> access) {
		if(access == FabricParticipants.FLUID_FILTERS) {
			return this.fluids;
		}
		return null;
	}
}

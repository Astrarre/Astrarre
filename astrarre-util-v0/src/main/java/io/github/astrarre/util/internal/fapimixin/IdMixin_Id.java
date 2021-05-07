package io.github.astrarre.util.internal.fapimixin;

import io.github.astrarre.util.v0.api.Id;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(io.github.minecraftcursedlegacy.api.registry.Id.class)
public abstract class IdMixin_Id implements Id {
	@Shadow public abstract String getNamespace();

	@Shadow public abstract String getName();

	@Override
	public String mod() {
		return this.getNamespace();
	}

	@Override
	public String path() {
		return this.getName();
	}
}

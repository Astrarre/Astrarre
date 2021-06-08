package io.github.astrarre.util.internal.mixin;

import io.github.astrarre.util.v0.api.Id;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.Identifier;

@Mixin(Identifier.class)
public abstract class IdentifierMixin_Id implements Id {
	@Shadow public abstract String getNamespace();
	@Shadow public abstract String getPath();

	@Override
	public String mod() {
		return this.getNamespace();
	}

	@Override
	public String path() {
		return this.getPath();
	}
}

package io.github.astrarre.components.internal.mixin;

import io.github.astrarre.components.internal.access.CopyAccess;
import io.github.astrarre.components.internal.access.DataObjectHolder;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public class EntityMixin implements DataObjectHolder {
	protected CopyAccess object;
	protected int version;

	@Override
	public CopyAccess astrarre_getObject() {
		return this.object;
	}

	@Override
	public int astrarre_getVersion() {
		return this.version;
	}

	@Override
	public void astrarre_setObject(CopyAccess object, int version) {
		this.object = object;
		this.version = version;
	}
}

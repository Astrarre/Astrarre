package io.github.astrarre.components.internal.mixin;

import io.github.astrarre.components.internal.lazyAsm.standard.CopyAccess;
import io.github.astrarre.components.v0.api.factory.DataObjectHolder;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.entity.BlockEntity;

@Mixin (BlockEntity.class)
public class BlockEntityMixin implements DataObjectHolder {
	// todo
	public CopyAccess astrarre_access;
	public int astrarre_version;

	@Override
	public CopyAccess astrarre_getObject() {
		return this.astrarre_access;
	}

	@Override
	public int astrarre_getVersion() {
		return this.astrarre_version;
	}

	@Override
	public void astrarre_setObject(CopyAccess object, int version) {
		this.astrarre_access = object;
		this.astrarre_version = version;
	}
}

package io.github.astrarre.components.internal.lazyAsm;

import io.github.astrarre.components.internal.access.PlayerDataObjectHolder;
import io.github.astrarre.components.internal.lazyAsm.standard.CopyAccess;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerDataObjectHolderComponentFactory extends DataObjectHolderComponentFactory<PlayerEntity> {
	public PlayerDataObjectHolderComponentFactory(String modid, String path) {
		super(modid, path);
	}

	@Override
	protected int getVersion(PlayerEntity context) {
		return ((PlayerDataObjectHolder) context).astrarre_getVersion_p();
	}

	@Override
	protected Object getData(PlayerEntity context) {
		return ((PlayerDataObjectHolder) context).astrarre_getObject_p();
	}


	@Override
	protected void setData(PlayerEntity context, Object data, int version) {
		((PlayerDataObjectHolder) context).astrarre_setObject_p((CopyAccess) data, version);
	}
}

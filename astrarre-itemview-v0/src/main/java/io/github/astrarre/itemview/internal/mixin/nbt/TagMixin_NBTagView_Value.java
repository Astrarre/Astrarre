package io.github.astrarre.itemview.internal.mixin.nbt;

import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.nbt.Tag;
import net.minecraft.util.io.AbstractTag;

@Mixin(AbstractTag.class)
public interface TagMixin_NBTagView_Value extends NbtValue {
	@Override
	default <T> T get(NBTType<T> type) {
		return FabricViews.view((AbstractTag) this, type);
	}
}

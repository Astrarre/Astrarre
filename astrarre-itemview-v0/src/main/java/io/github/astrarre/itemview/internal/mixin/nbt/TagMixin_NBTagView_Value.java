package io.github.astrarre.itemview.internal.mixin.nbt;

import java.util.Iterator;
import java.util.List;

import io.github.astrarre.itemview.internal.util.ImmutableIterable;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import net.minecraft.nbt.NbtElement;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NbtElement.class)
public interface TagMixin_NBTagView_Value extends NbtValue {
	@Override
	default <T> T get(NBTType<T> type) {
		return FabricViews.view((NbtElement) this, type);
	}


}

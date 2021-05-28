package io.github.astrarre.itemview.internal.mixin.nbt.list;

import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import net.minecraft.nbt.NbtLongArray;
import io.github.astrarre.itemview.internal.access.AbstractListTagAccess;
import io.github.astrarre.itemview.internal.nbt.list.LongArrayView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin (NbtLongArray.class)
public class LongArrayTagMixin implements AbstractListTagAccess {
	private Object view;

	@Override
	public Object itemview_getListTag(NBTType<?> type) {
		if(type != NBTType.LONG_ARRAY) {
			throw new IllegalArgumentException("type is not of int array!");
		}

		Object view = this.view;
		if (view == null) {
			this.view = view = LongArrayView.create((NbtLongArray) (Object) this);
		}
		return view;
	}
}

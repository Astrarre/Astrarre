package io.github.astrarre.itemview.internal.mixin.nbt.list;

import io.github.astrarre.itemview.internal.access.AbstractListTagAccess;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import net.minecraft.nbt.NbtIntArray;
import io.github.astrarre.itemview.internal.nbt.list.IntArrayView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NbtIntArray.class)
public class IntArrayTagMixin implements AbstractListTagAccess {
	private Object view;

	@Override
	public Object itemview_getListTag(NBTType<?> type) {
		if(type != NBTType.INT_ARRAY) {
			throw new IllegalArgumentException("type is not of int array!");
		}

		Object view = this.view;
		if (view == null) {
			this.view = view = IntArrayView.create((NbtIntArray) (Object) this);
		}
		return view;
	}
}

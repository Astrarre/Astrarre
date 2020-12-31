package io.github.astrarre.itemview.internal.mixin.nbt.list;

import io.github.astrarre.itemview.internal.access.AbstractListTagAccess;
import io.github.astrarre.itemview.internal.nbt.list.ByteArrayView;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.nbt.ByteArrayTag;

@Mixin (ByteArrayTag.class)
public class ByteArrayTagMixin implements AbstractListTagAccess {
	private Object view;

	@Override
	public Object itemview_getListTag(NBTType<?> type) {
		if(type != NBTType.BYTE_ARRAY) {
			throw new IllegalArgumentException("type is not of byte array!");
		}

		Object view = this.view;
		if (view == null) {
			this.view = view = ByteArrayView.create((ByteArrayTag) (Object) this);
		}
		return view;
	}
}

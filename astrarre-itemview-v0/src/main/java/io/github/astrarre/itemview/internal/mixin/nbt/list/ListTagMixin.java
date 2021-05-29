package io.github.astrarre.itemview.internal.mixin.nbt.list;

import io.github.astrarre.itemview.internal.access.AbstractListTagAccess;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import net.minecraft.nbt.NbtList;
import io.github.astrarre.itemview.internal.nbt.list.ListTagView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NbtList.class)
public abstract class ListTagMixin implements AbstractListTagAccess {
	@Shadow public abstract byte getType();
	@Shadow public abstract byte getHeldType();

	private Object view;

	@Override
	public Object itemview_getListTag(NBTType<?> type) {
		NBTType<?> component = type == null ? null : type.getComponent();
		if(type != null) {
			if (component == null || !(type.internalTypeEquals(this.getType()) && component.internalTypeEquals(this.getHeldType()))) {
				throw new IllegalArgumentException("NbtType does not reflect list type!");
			}
		}

		Object view = this.view;
		if (view == null) {
			this.view = view = ListTagView.create((NbtList) (Object) this, component);
		}
		return view;
	}
}

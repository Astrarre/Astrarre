package io.github.astrarre.itemview.internal.mixin.nbt.list;

import io.github.astrarre.itemview.internal.access.AbstractListTagAccess;
import io.github.astrarre.itemview.internal.nbt.list.ListTagView;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.nbt.ListTag;

@Mixin(ListTag.class)
public abstract class ListTagMixin implements AbstractListTagAccess {

	@Shadow public abstract byte getType();

	@Shadow public abstract byte getElementType();

	private Object view;

	@Override
	public Object itemview_getListTag(NBTType<?> type) {
		NBTType<?> component = type.getComponent();
		if(component == null || !(type.internalTypeEquals(this.getType()) && component.internalTypeEquals(this.getElementType()))) {
			throw new IllegalArgumentException("NbtType does not reflect list type!");
		}

		Object view = this.view;
		if (view == null) {
			this.view = view = ListTagView.create((ListTag) (Object) this, component);
		}
		return view;
	}
}

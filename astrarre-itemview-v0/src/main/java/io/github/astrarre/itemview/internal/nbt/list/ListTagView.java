package io.github.astrarre.itemview.internal.nbt.list;

import java.util.AbstractList;

import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.ListTag;

public class ListTagView extends AbstractList<Object> {
	private final ListTag tag;
	@Nullable
	private final NBTType<?> component;

	public ListTagView(ListTag tag, @Nullable NBTType<?> component) {
		this.tag = tag;
		this.component = component;
	}

	/**
	 * @see FabricViews#view(ListTag, NBTType)
	 * @deprecated internal
	 */
	@Deprecated
	public static ListTagView create(ListTag tag, NBTType<?> component) {
		return new ListTagView(tag, component);
	}

	@Override
	public Object get(int index) {
		return FabricViews.view(this.tag.get(index), this.component);
	}

	@Override
	public int size() {
		return this.tag.size();
	}
}

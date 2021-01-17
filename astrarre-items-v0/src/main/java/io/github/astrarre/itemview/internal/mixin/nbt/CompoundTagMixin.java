package io.github.astrarre.itemview.internal.mixin.nbt;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.github.astrarre.itemview.internal.FabricViews;
import io.github.astrarre.itemview.internal.access.ImmutableAccess;
import io.github.astrarre.itemview.internal.util.ImmutableIterable;
import io.github.astrarre.itemview.internal.util.NBTagUnmodifiableMap;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

@Mixin(CompoundTag.class)
public abstract class CompoundTagMixin implements NBTagView, ImmutableAccess {
	private boolean immutable;
	@Shadow public abstract @Nullable Tag shadow$get(String key);
	@Shadow public abstract Set<String> getKeys();
	@Shadow public abstract CompoundTag getCompound(String key);
	@Shadow public abstract CompoundTag shadow$copy();
	@Shadow public abstract boolean shadow$isEmpty();

	@Mutable @Shadow @Final private Map<String, Tag> tags;

	@Override
	@Intrinsic
	public boolean isEmpty() {
		return this.shadow$isEmpty();
	}

	@Override
	public byte getByte(String path, byte def) {
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.BYTE);
		if(tag != null) {
			return tag.getByte();
		}
		return def;
	}

	@Override
	public short getShort(String path, short def) {
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.SHORT);
		if(tag != null) {
			return tag.getShort();
		}
		return def;
	}

	@Override
	public int getInt(String path, int def) {
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.INT);
		if(tag != null) {
			return tag.getInt();
		}
		return def;
	}

	@Override
	public float getFloat(String path, float def) {
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.FLOAT);
		if(tag != null) {
			return tag.getFloat();
		}
		return def;
	}

	@Override
	public long getLong(String path, long def) {
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.LONG);
		if(tag != null) {
			return tag.getLong();
		}
		return def;
	}

	@Override
	public double getDouble(String path, double def) {
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.DOUBLE);
		if(tag != null) {
			return tag.getDouble();
		}
		return def;
	}

	@Override
	public Number getNumber(String path, Number def) {
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.DOUBLE);
		if(tag != null) {
			return tag.getNumber();
		}
		return def;
	}

	@Nullable
	private AbstractNumberTag itemview_getTag(String path, NBTType<?> type) {
		Tag tag = this.shadow$get(path);
		if(tag != null && tag.getType() == type.getInternalType() && tag instanceof AbstractNumberTag) {
			return ((AbstractNumberTag)tag);
		}
		return null;
	}

	@Override
	public String getString(String path, String def) {
		Tag tag = this.shadow$get(path);
		if(tag != null && tag.getType() == NBTType.STRING.getInternalType()) {
			return tag.asString();
		}
		return def;
	}

	@Override
	public Object get(String key) {
		return FabricViews.view(this.shadow$get(key), NBTType.ANY);
	}

	@Override
	public NBTagView getTag(String path, NBTagView def) {
		CompoundTag tag = this.getCompound(path);
		return tag == null ? null : FabricViews.view(tag);
	}

	@Override
	public <T> T get(String path, NBTType<T> type, T def) {
		Tag tag = this.shadow$get(path);
		if(tag == null) {
			return def;
		}
		try {
			return FabricViews.view(tag, type);
		} catch (ClassCastException e) {
			return def;
		}
	}

	@Override
	public Iterator<String> iterator() {
		return new ImmutableIterable<>(this.getKeys().iterator());
	}

	@Override
	public NBTagView copy() {
		if(this.immutable) {
			return this;
		}
		return FabricViews.view(this.shadow$copy());
	}

	@Override
	public void astrarre_setImmutable() {
		if(this.immutable = false) {
			this.immutable = true;
			this.tags = new NBTagUnmodifiableMap(this.tags);
		}
	}

	@Override
	public boolean astrarre_isImmutable() {
		return this.immutable;
	}
}

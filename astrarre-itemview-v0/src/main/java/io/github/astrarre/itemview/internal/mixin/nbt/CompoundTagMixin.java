package io.github.astrarre.itemview.internal.mixin.nbt;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.astrarre.itemview.v0.api.item.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.item.nbt.NBTagView;
import io.github.astrarre.itemview.internal.access.ImmutableAccess;
import io.github.astrarre.itemview.internal.util.ImmutableIterable;
import io.github.astrarre.itemview.internal.util.NBTagUnmodifiableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

@Mixin(CompoundTag.class)
@Implements(@Interface(prefix = "internal$", iface = NBTagView.class))
public abstract class CompoundTagMixin implements ImmutableAccess {
	private boolean immutable;
	@Shadow public abstract @Nullable Tag get(String key);
	@Shadow public abstract Set<String> getKeys();
	@Shadow public abstract CompoundTag getCompound(String key);
	@Shadow public abstract CompoundTag shadow$copy();
	@Shadow public abstract boolean shadow$isEmpty();

	@Mutable @Shadow @Final private Map<String, Tag> tags;

	@Intrinsic
	public boolean internal$isEmpty() {
		return this.shadow$isEmpty();
	}

	public byte internal$getByte(String path, byte def) {
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.BYTE);
		if(tag != null) {
			return tag.getByte();
		}
		return def;
	}

	public short internal$getShort(String path, short def) {
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.SHORT);
		if(tag != null) {
			return tag.getShort();
		}
		return def;
	}

	public int internal$getInt(String path, int def) {
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.INT);
		if(tag != null) {
			return tag.getInt();
		}
		return def;
	}

	public float internal$getFloat(String path, float def) {
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.FLOAT);
		if(tag != null) {
			return tag.getFloat();
		}
		return def;
	}

	public long internal$getLong(String path, long def) {
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.LONG);
		if(tag != null) {
			return tag.getLong();
		}
		return def;
	}

	public double internal$getDouble(String path, double def) {
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.DOUBLE);
		if(tag != null) {
			return tag.getDouble();
		}
		return def;
	}

	public Number internal$getNumber(String path, Number def) {
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.NUMBER);
		if(tag != null) {
			return tag.getNumber();
		}
		return def;
	}

	@Nullable
	@Unique
	private AbstractNumberTag itemview_getTag(String path, NBTType<?> type) {
		Tag tag = this.get(path);
		if(tag != null && (tag.getType() == type.getInternalType() || type == NBTType.NUMBER) && tag instanceof AbstractNumberTag) {
			return ((AbstractNumberTag)tag);
		}
		return null;
	}

	public String internal$getString(String path, String def) {
		Tag tag = this.get(path);
		if(tag != null && tag.getType() == NBTType.STRING.getInternalType()) {
			return tag.asString();
		}
		return def;
	}

	public Object internal$get(String key) {
		return FabricViews.view(this.get(key), NBTType.ANY);
	}

	public NBTagView internal$getTag(String path, NBTagView def) {
		CompoundTag tag = this.getCompound(path);
		return tag == null ? null : FabricViews.view(tag);
	}

	public <T> T internal$get(String path, NBTType<T> type, T def) {
		Tag tag = this.get(path);
		if(tag == null) {
			return def;
		}
		try {
			return FabricViews.view(tag, type);
		} catch (ClassCastException e) {
			return def;
		}
	}

	@NotNull
	public Iterator<String> internal$iterator() {
		return new ImmutableIterable<>(this.getKeys().iterator());
	}

	public NBTagView internal$copy() {
		if(this.immutable) {
			return (NBTagView) this;
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

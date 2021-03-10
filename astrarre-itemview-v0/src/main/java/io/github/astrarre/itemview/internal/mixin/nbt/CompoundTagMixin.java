package io.github.astrarre.itemview.internal.mixin.nbt;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.github.astrarre.itemview.internal.access.ImmutableAccess;
import io.github.astrarre.itemview.internal.util.ImmutableIterable;
import io.github.astrarre.itemview.internal.util.NBTagUnmodifiableMap;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

@Mixin(CompoundTag.class)
public abstract class CompoundTagMixin implements ImmutableAccess, NBTagView.Builder {
	private boolean immutable;
	@Shadow public abstract @Nullable Tag shadow$get(String key);
	@Shadow public abstract Set<String> getKeys();
	@Shadow public abstract CompoundTag getCompound(String key);
	@Shadow public abstract boolean shadow$isEmpty();

	@Mutable @Shadow @Final private Map<String, Tag> tags;

	@Shadow public abstract void putByte(String key, byte value);

	@Shadow public abstract void putBoolean(String key, boolean value);

	@Shadow public abstract void putShort(String key, short value);

	@Shadow public abstract void putFloat(String key, float value);

	@Shadow public abstract void putInt(String key, int value);

	@Shadow public abstract void putDouble(String key, double value);

	@Shadow public abstract void putLong(String key, long value);

	@Shadow @Nullable public abstract Tag put(String key, Tag tag);

	@Intrinsic
	public boolean soft$isEmpty() {
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
		AbstractNumberTag tag = this.itemview_getTag(path, NBTType.NUMBER);
		if(tag != null) {
			return tag.getNumber();
		}
		return def;
	}

	@Nullable
	@Unique
	private AbstractNumberTag itemview_getTag(String path, NBTType<?> type) {
		Tag tag = this.shadow$get(path);
		if(tag != null && (tag.getType() == type.getInternalType() || type == NBTType.NUMBER) && tag instanceof AbstractNumberTag) {
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
	@NotNull
	public Iterator<String> iterator() {
		return new ImmutableIterable<>(this.getKeys().iterator());
	}

	@Override
	public NBTagView copy() {
		if(this.immutable) {
			return this;
		}
		return FabricViews.immutableView((CompoundTag) (Object) this);
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

	@Override
	public Builder set(String key, byte b) {
		this.putByte(key, b);
		return this;
	}

	@Override
	public Builder set(String key, boolean b) {
		this.putBoolean(key, b);
		return this;
	}

	@Override
	public Builder set(String key, char c) {
		this.putShort(key, (short) c);
		return this;
	}

	@Override
	public Builder set(String key, short s) {
		this.putShort(key, s);
		return this;
	}

	@Override
	public Builder set(String key, float f) {
		this.putFloat(key, f);
		return this;
	}

	@Override
	public Builder set(String key, int i) {
		this.putInt(key, i);
		return this;
	}

	@Override
	public Builder set(String key, double d) {
		this.putDouble(key, d);
		return this;
	}

	@Override
	public Builder set(String key, long l) {
		this.putLong(key, l);
		return this;
	}

	@Override
	public <T> Builder set(String path, NBTType<T> type, T object) {
		this.put(path, FabricViews.from(object));
		return this;
	}

	@Override
	public NBTagView build() {
		return this.copy();
	}
}

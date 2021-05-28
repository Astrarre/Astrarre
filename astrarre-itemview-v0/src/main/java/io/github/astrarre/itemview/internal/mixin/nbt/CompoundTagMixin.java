package io.github.astrarre.itemview.internal.mixin.nbt;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import io.github.astrarre.itemview.internal.access.ImmutableAccess;
import io.github.astrarre.itemview.internal.util.ImmutableIterable;
import io.github.astrarre.itemview.internal.util.NBTagUnmodifiableMap;
import io.github.astrarre.itemview.v0.api.nbt.NBTType;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(NbtCompound.class)
public abstract class CompoundTagMixin implements ImmutableAccess, NBTagView.Builder {
	private boolean immutable;
	@Shadow public abstract @Nullable NbtElement shadow$get(String key);
	@Shadow public abstract Set<String> getKeys();
	@Shadow public abstract NbtCompound getCompound(String key);
	@Shadow public abstract boolean shadow$isEmpty();

	@Mutable @Shadow @Final private Map<String, NbtElement> tags;

	@Shadow public abstract void shadow$putByte(String key, byte value);

	@Shadow public abstract void shadow$putBoolean(String key, boolean value);

	@Shadow public abstract void shadow$putShort(String key, short value);

	@Shadow public abstract void shadow$putFloat(String key, float value);

	@Shadow public abstract void shadow$putInt(String key, int value);

	@Shadow public abstract void shadow$putDouble(String key, double value);

	@Shadow public abstract void shadow$putLong(String key, long value);

	@Shadow @Nullable public abstract NbtElement put(String key, NbtElement tag);

	@Intrinsic
	public boolean soft$isEmpty() {
		return this.shadow$isEmpty();
	}

	@Override
	public byte getByte(String path, byte def) {
		AbstractNbtNumber tag = this.itemview_getTag(path, NBTType.BYTE);
		if(tag != null) {
			return tag.byteValue();
		}
		return def;
	}

	@Override
	public short getShort(String path, short def) {
		AbstractNbtNumber tag = this.itemview_getTag(path, NBTType.SHORT);
		if(tag != null) {
			return tag.shortValue();
		}
		return def;
	}

	@Override
	public int getInt(String path, int def) {
		AbstractNbtNumber tag = this.itemview_getTag(path, NBTType.INT);
		if(tag != null) {
			return tag.intValue();
		}
		return def;
	}

	@Override
	public float getFloat(String path, float def) {
		AbstractNbtNumber tag = this.itemview_getTag(path, NBTType.FLOAT);
		if(tag != null) {
			return tag.floatValue();
		}
		return def;
	}

	@Override
	public long getLong(String path, long def) {
		AbstractNbtNumber tag = this.itemview_getTag(path, NBTType.LONG);
		if(tag != null) {
			return tag.longValue();
		}
		return def;
	}

	@Override
	public double getDouble(String path, double def) {
		AbstractNbtNumber tag = this.itemview_getTag(path, NBTType.DOUBLE);
		if(tag != null) {
			return tag.doubleValue();
		}
		return def;
	}

	@Override
	public Number getNumber(String path, Number def) {
		AbstractNbtNumber tag = this.itemview_getTag(path, NBTType.NUMBER);
		if(tag != null) {
			return tag.numberValue();
		}
		return def;
	}

	@Nullable
	@Unique
	private AbstractNbtNumber itemview_getTag(String path, NBTType<?> type) {
		NbtElement tag = this.shadow$get(path);
		if(tag != null && (tag.getType() == type.getInternalType() || type == NBTType.NUMBER) && tag instanceof AbstractNbtNumber) {
			return ((AbstractNbtNumber)tag);
		}
		return null;
	}

	@Override
	public String getString(String path, String def) {
		NbtElement tag = this.shadow$get(path);
		if(tag != null && tag.getType() == NBTType.STRING.getInternalType()) {
			return tag.asString();
		}
		return def;
	}

	@Override
	public NbtValue getValue(String path) {
		return (NbtValue) this.shadow$get(path);
	}

	@Override
	public Builder putValue(String path, NbtValue value) {
		this.put(path, (NbtElement) value);
		return this;
	}

	@Override
	@Intrinsic
	public Object get(String key) {
		return FabricViews.view(this.shadow$get(key), NBTType.ANY);
	}

	@Override
	public NBTagView getTag(String path, NBTagView def) {
		NbtCompound tag = this.getCompound(path);
		return tag == null ? null : FabricViews.view(tag);
	}

	@Override
	public <T> T get(String path, NBTType<T> type, T def) {
		NbtElement tag = this.shadow$get(path);
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
		return FabricViews.immutableView((NbtCompound) (Object) this);
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
	public Builder putByte(String key, byte b) {
		this.shadow$putByte(key, b);
		return this;
	}

	@Override
	public Builder putBool(String key, boolean b) {
		this.shadow$putBoolean(key, b);
		return this;
	}

	@Override
	public Builder putChar(String key, char c) {
		this.shadow$putShort(key, (short) c);
		return this;
	}

	@Override
	public Builder putShort(String key, short s) {
		this.shadow$putShort(key, s);
		return this;
	}

	@Override
	public Builder putFloat(String key, float f) {
		this.shadow$putFloat(key, f);
		return this;
	}

	@Override
	public Builder putInt(String key, int i) {
		this.shadow$putInt(key, i);
		return this;
	}

	@Override
	public Builder putDouble(String key, double d) {
		this.shadow$putDouble(key, d);
		return this;
	}

	@Override
	public Builder putLong(String key, long l) {
		this.shadow$putLong(key, l);
		return this;
	}

	@Override
	public <T> Builder put(String path, NBTType<T> type, T object) {
		this.put(path, FabricViews.from(object));
		return this;
	}

	@Override
	public NBTagView build() {
		return this.copy();
	}
}

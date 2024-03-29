package io.github.astrarre.components.v0.fabric;

import java.io.IOException;

import io.github.astrarre.components.v0.api.components.BoolComponent;
import io.github.astrarre.components.v0.api.components.ByteComponent;
import io.github.astrarre.components.v0.api.components.CharComponent;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.components.DoubleComponent;
import io.github.astrarre.components.v0.api.components.FloatComponent;
import io.github.astrarre.components.v0.api.components.IntComponent;
import io.github.astrarre.components.v0.api.components.LongComponent;
import io.github.astrarre.components.v0.api.components.ShortComponent;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NbtValue;
import io.github.astrarre.util.v0.api.func.Copier;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;
import net.minecraft.network.PacketByteBuf;

/**
 * @implNote separating the component factories by behavior instead of telling the factory behavior wasn't just an API decision, it's also better
 * 		for performance
 */
@SuppressWarnings("unchecked")
public final class FabricComponents {
	public static <C, V> void serialize(NbtCompound compound, String key, C context, Component<C, V> component, Serializer<V> serializer) {
		NbtElement element = serialize(context, component, serializer);
		if(element != null) {
			compound.put(key, element);
		}
	}

	@Nullable
	public static <C, V> NbtElement serialize(C context, Component<C, V> component, Serializer<V> serializer) {
		if(serializer == null) {
			if(component instanceof BoolComponent) {
				boolean val = ((BoolComponent<C>) component).getBool(context);
				if(val) {
					return NbtByte.of(true);
				}
			} else if(component instanceof ByteComponent) {
				var val = ((ByteComponent<C>) component).getByte(context);
				if(val != 0) {
					return NbtByte.of(val);
				}
			} else if(component instanceof CharComponent) {
				var val = (short) ((CharComponent<C>) component).getChar(context);
				if(val != 0) {
					return NbtShort.of(val);
				}
			} else if(component instanceof DoubleComponent) {
				var val = ((DoubleComponent<C>) component).getDouble(context);
				if(val != 0) {
					return NbtDouble.of(val);
				}
			} else if(component instanceof FloatComponent) {
				var val = ((FloatComponent<C>) component).getFloat(context);
				if(val != 0) {
					return NbtFloat.of(val);
				}
			} else if(component instanceof IntComponent) {
				var val = ((IntComponent<C>) component).getInt(context);
				if(val != 0) {
					return NbtInt.of(val);
				}
			} else if(component instanceof LongComponent) {
				var val = ((LongComponent<C>) component).getLong(context);
				if(val != 0) {
					return NbtLong.of(val);
				}
			} else if(component instanceof ShortComponent) {
				var val = ((ShortComponent<C>) component).getShort(context);
				if(val != 0) {
					return NbtShort.of(val);
				}
			} else {
				throw new IllegalArgumentException("copier cannot be null!");
			}
		} else {
			V value = component.get(context);
			if(value != null || serializer.serializesNulls()) {
				return serializer.save(value).asMinecraft();
			}
		}
		return null;
	}

	public static <C, V, T extends Component<C, V>> void deserialize(NbtElement element, C context, T component, Serializer<V> serializer) {
		if(serializer == null) {
			if(component instanceof BoolComponent) {
				((BoolComponent<C>) component).setBool(context, ((AbstractNbtNumber) element).byteValue() != 0);
			} else if(component instanceof ByteComponent) {
				((ByteComponent<C>) component).setByte(context, ((AbstractNbtNumber) element).byteValue());
			} else if(component instanceof CharComponent) {
				((CharComponent<C>) component).setChar(context, (char) ((AbstractNbtNumber) element).shortValue());
			} else if(component instanceof DoubleComponent) {
				((DoubleComponent<C>) component).setDouble(context, ((AbstractNbtNumber) element).doubleValue());
			} else if(component instanceof FloatComponent) {
				((FloatComponent<C>) component).setFloat(context, ((AbstractNbtNumber) element).floatValue());
			} else if(component instanceof IntComponent) {
				((IntComponent<C>) component).setInt(context, ((AbstractNbtNumber) element).intValue());
			} else if(component instanceof LongComponent) {
				((LongComponent<C>) component).setLong(context, ((AbstractNbtNumber) element).longValue());
			} else if(component instanceof ShortComponent) {
				((ShortComponent<C>) component).setShort(context, ((AbstractNbtNumber) element).shortValue());
			} else {
				throw new IllegalArgumentException("copier cannot be null!");
			}
		} else {
			component.set(context, serializer.read((NbtValue) element));
		}
	}


	public static <C, V, T extends Component<C, V>> void copy(C fromContext, C toContext, T component, Copier<V> copier) {
		if(copier == null) {
			if(component instanceof BoolComponent) {
				((BoolComponent<C>) component).setBool(toContext, ((BoolComponent<C>) component).getBool(fromContext));
			} else if(component instanceof ByteComponent) {
				((ByteComponent<C>) component).setByte(toContext, ((ByteComponent<C>) component).getByte(fromContext));
			} else if(component instanceof CharComponent) {
				((CharComponent<C>) component).setChar(toContext, ((CharComponent<C>) component).getChar(fromContext));
			} else if(component instanceof DoubleComponent) {
				((DoubleComponent<C>) component).setDouble(toContext, ((DoubleComponent<C>) component).getDouble(fromContext));
			} else if(component instanceof FloatComponent) {
				((FloatComponent<C>) component).setFloat(toContext, ((FloatComponent<C>) component).getFloat(fromContext));
			} else if(component instanceof IntComponent) {
				((IntComponent<C>) component).setInt(toContext, ((IntComponent<C>) component).getInt(fromContext));
			} else if(component instanceof LongComponent) {
				((LongComponent<C>) component).setLong(toContext, ((LongComponent<C>) component).getLong(fromContext));
			} else if(component instanceof ShortComponent) {
				((ShortComponent<C>) component).setShort(toContext, ((ShortComponent<C>) component).getShort(fromContext));
			} else {
				throw new IllegalArgumentException("copier cannot be null!");
			}
		} else {
			V value = copier.copy(component.get(fromContext));
			if(value != null || copier.copiesNulls()) {
				component.set(toContext, value);
			}
		}
	}

	public static <C, V, T extends Component<C, V>> void serialize(PacketByteBuf buf, C context, T component, FabricByteSerializer<V> serializer)
			throws IOException {
		if(serializer == null) {
			if(component instanceof BoolComponent) {
				buf.writeBoolean(((BoolComponent<C>) component).getBool(context));
			} else if(component instanceof ByteComponent) {
				buf.writeByte(((ByteComponent<C>) component).getByte(context));
			} else if(component instanceof CharComponent) {
				buf.writeChar((short) ((CharComponent<C>) component).getChar(context));
			} else if(component instanceof DoubleComponent) {
				buf.writeDouble(((DoubleComponent<C>) component).getDouble(context));
			} else if(component instanceof FloatComponent) {
				buf.writeFloat(((FloatComponent<C>) component).getFloat(context));
			} else if(component instanceof IntComponent) {
				buf.writeInt(((IntComponent<C>) component).getInt(context));
			} else if(component instanceof LongComponent) {
				buf.writeLong(((LongComponent<C>) component).getLong(context));
			} else if(component instanceof ShortComponent) {
				buf.writeShort(((ShortComponent<C>) component).getShort(context));
			} else {
				throw new IllegalArgumentException("copier cannot be null!");
			}
		} else {
			serializer.toBytes(component.get(context), buf);
		}
	}

	public static <C, V, T extends Component<C, V>> void deserialize(PacketByteBuf buf, C context, T component, FabricByteSerializer<V> serializer)
			throws IOException {
		if(serializer == null) {
			if(component instanceof BoolComponent) {
				((BoolComponent<C>) component).setBool(context, buf.readBoolean());
			} else if(component instanceof ByteComponent) {
				((ByteComponent<C>) component).setByte(context, buf.readByte());
			} else if(component instanceof CharComponent) {
				((CharComponent<C>) component).setChar(context, buf.readChar());
			} else if(component instanceof DoubleComponent) {
				((DoubleComponent<C>) component).setDouble(context, buf.readDouble());
			} else if(component instanceof FloatComponent) {
				((FloatComponent<C>) component).setFloat(context, buf.readFloat());
			} else if(component instanceof IntComponent) {
				((IntComponent<C>) component).setInt(context, buf.readInt());
			} else if(component instanceof LongComponent) {
				((LongComponent<C>) component).setLong(context, buf.readLong());
			} else if(component instanceof ShortComponent) {
				((ShortComponent<C>) component).setShort(context, buf.readShort());
			} else {
				throw new IllegalArgumentException("copier cannot be null!");
			}
		} else {
			serializer.toBytes(component.get(context), buf);
		}
	}

	// todo for ItemStack
	// create a custom map from String -> Tag that creates and caches tags for the Components, requires callback to invalidate cached tags.
	// non-string objects will unfortunately need to be reserialized every time it's accessed because they may be mutable :sad_tater:
	// then, mixin to CompoundTag and check if the map is an instance of our special map, if so, optimize by not grabbing the tag
}

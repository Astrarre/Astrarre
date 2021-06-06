package io.github.astrarre.components.v0.fabric;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.components.v0.api.Copier;
import io.github.astrarre.components.v0.api.components.*;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.api.factory.ComponentManager;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;
import net.minecraft.util.Pair;

/**
 * @implNote separating the component factories by behavior instead of telling the factory behavior wasn't just an API decision, it's also better
 * 		for performance
 */
@SuppressWarnings ("unchecked")
public final class FabricComponents {

	/**
	 * Not serialized/deserialized from NBT. Not invalidated if the block entity is invalidated and re-validated.
	 */
	public static final ComponentManager<BlockEntity> BLOCK_ENTITY_NO_SERIALIZE = ComponentManager.newManager("astrarre-components-v0", "blockentity");

	public static <C, V, T extends Component<C, V>, E extends NbtElement> E serialize(C context, T component, FabricSerializer<V, E> serializer) {
		if(serializer == null) {
			if(component instanceof BoolComponent) {
				return (E) NbtByte.of(((BoolComponent<C>) component).getBool(context));
			} else if(component instanceof ByteComponent) {
				return (E) NbtByte.of(((ByteComponent<C>) component).getByte(context));
			} else if(component instanceof CharComponent) {
				return (E) NbtShort.of((short) ((CharComponent<C>) component).getChar(context));
			} else if(component instanceof DoubleComponent) {
				return (E) NbtDouble.of(((DoubleComponent<C>)component).getDouble(context));
			} else if(component instanceof FloatComponent) {
				return (E) NbtFloat.of(((FloatComponent<C>)component).getFloat(context));
			} else if(component instanceof IntComponent) {
				return (E) NbtInt.of(((IntComponent<C>)component).getInt(context));
			} else if(component instanceof LongComponent) {
				return (E) NbtLong.of(((LongComponent<C>)component).getLong(context));
			} else if(component instanceof ShortComponent) {
				return (E) NbtShort.of(((ShortComponent<C>)component).getShort(context));
			} else {
				throw new IllegalArgumentException("copier cannot be null!");
			}
		} else {
			return serializer.toTag(component.get(context));
		}
	}

	public static <C, V, T extends Component<C, V>, E extends NbtElement> void deserialize(E element, C context, T component, FabricSerializer<V, E> serializer) {
		if(serializer == null) {
			if(component instanceof BoolComponent) {
				((BoolComponent<C>)component).setBool(context, ((AbstractNbtNumber)element).byteValue() != 0);
			} else if(component instanceof ByteComponent) {
				((ByteComponent<C>)component).setByte(context, ((AbstractNbtNumber)element).byteValue());
			} else if(component instanceof CharComponent) {
				((CharComponent<C>)component).setChar(context, (char) ((AbstractNbtNumber)element).shortValue());
			} else if(component instanceof DoubleComponent) {
				((DoubleComponent<C>)component).setDouble(context, ((AbstractNbtNumber)element).doubleValue());
			} else if(component instanceof FloatComponent) {
				((FloatComponent<C>)component).setFloat(context, ((AbstractNbtNumber)element).floatValue());
			} else if(component instanceof IntComponent) {
				((IntComponent<C>)component).setInt(context, ((AbstractNbtNumber)element).intValue());
			} else if(component instanceof LongComponent) {
				((LongComponent<C>)component).setLong(context, ((AbstractNbtNumber)element).longValue());
			} else if(component instanceof ShortComponent) {
				((ShortComponent<C>)component).setShort(context, ((AbstractNbtNumber)element).shortValue());
			} else {
				throw new IllegalArgumentException("copier cannot be null!");
			}
		} else {
			component.set(context, serializer.fromTag(element));
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
			component.set(toContext, copier.copy(component.get(fromContext)));
		}
	}

	// todo for ItemStack
	// create a custom map from String -> Tag that creates and caches tags for the Components, requires callback to invalidate cached tags.
	// non-string objects will unfortunately need to be reserialized every time it's accessed because they may be mutable :sad_tater:
	// then, mixin to CompoundTag and check if the map is an instance of our special map, if so, optimize by not grabbing the tag
}

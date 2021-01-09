package io.github.astrarre.itemview.v0.api.nbt;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.github.astrarre.itemview.internal.FabricViews;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;

/**
 * an unmodifiable view of an nbt tag, the underlying object is not guaranteed to be immutable and may change.
 * @see FabricViews
 */
public interface NBTagView extends Iterable<String> {
	NBTagView EMPTY = FabricViews.view(new CompoundTag());
	// todo view all keys

	/**
	 * equivalent to
	 * <code>
	 *     view.getByte(path, 0)
	 * </code>
	 */
	default byte getByte(String path) {
		return this.getByte(path, (byte)0);
	}

	/**
	 * equivalent to
	 * <code>
	 *     view.getByte(path, (byte)0)
	 * </code>
	 */
	default byte getByte(String path, int def) {
		return this.getByte(path, (byte) def);
	}

	/**
	 * @param def if there is no entry with type byte for the path, the method will return this value
	 */
	byte getByte(String path, byte def);

	/**
	 * equivalent to
	 * <code>
	 *     view.getBool(path, false)
	 * </code>
	 */
	default boolean getBool(String path) {
		return this.getBool(path, false);
	}

	/**
	 * @param def if there is no entry with type byte for the path, the method will return this value
	 */
	boolean getBool(String path, boolean def);

	/**
	 * equivalent to
	 * <code>
	 *     view.getShort(path, 0)
	 * </code>
	 */
	default short getShort(String path) {
		return this.getShort(path, (short) 0);
	}

	/**
	 * equivalent to
	 * <code>
	 *     view.getShort(path, (short)0)
	 * </code>
	 */
	default short getShort(String path, int def) {
		return this.getShort(path, (short) def);
	}

	/**
	 * @param def if there is no entry with type short for the path, the method will return this value
	 */
	short getShort(String path, short def);

	/**
	 * equivalent to
	 * <code>
	 *     view.getChar(path, '\0')
	 * </code>
	 */
	default char getChar(String path) {
		return this.getChar(path, '\0');
	}
	
	/**
	 * @param def if there is no entry with type short for the path, the method will return this value
	 */
	char getChar(String path, char def);

	/**
	 * equivalent to
	 * <code>
	 *     view.getInt(path, 0)
	 * </code>
	 */
	default int getInt(String path) {
		return this.getInt(path, 0);
	}
	
	/**
	 * @param def if there is no entry with type int for the path, the method will return this value
	 */
	int getInt(String path, int def);

	/**
	 * equivalent to
	 * <code>
	 *     view.getFloat(path, 0)
	 * </code>
	 */
	default float getFloat(String path) {
		return this.getFloat(path, 0);
	}
	
	/**
	 * @param def if there is no entry with type float for the path, the method will return this value
	 */
	float getFloat(String path, float def);

	/**
	 * equivalent to
	 * <code>
	 *     view.getLong(path, 0)
	 * </code>
	 */
	default long getLong(String path) {
		return this.getLong(path, 0);
	}
	
	/**
	 * @param def if there is no entry with type long for the path, the method will return this value
	 */
	long getLong(String path, long def);

	/**
	 * equivalent to
	 * <code>
	 *     view.getDouble(path, 0)
	 * </code>
	 */
	default double getDouble(String path) {
		return this.getDouble(path, 0);
	}
	
	/**
	 * @param def if there is no entry with type double for the path, the method will return this value
	 */
	double getDouble(String path, double def);

	/**
	 * equivalent to
	 * <code>
	 *     view.getNumber(path, 0)
	 * </code>
	 */
	default Number getNumber(String path) {
		return this.getNumber(path, 0);
	}

	/**
	 * @param def if there is no entry with type number for the path, the method will return this value
	 */
	Number getNumber(String path, Number def);

	/**
	 * equivalent to
	 * <code>
	 *     view.getString(path, "")
	 * </code>
	 */
	default String getString(String path) {
		return this.getString(path, "");
	}
	
	/**
	 * @param def if there is no entry with type string for the path, the method will return this value
	 */
	String getString(String path, String def);

	/**
	 * if in the future multiple objects of different types can have the same key, it'll choose one at random
	 * if some keys don't have their own nbt representation (at the time of this writing) so they will default to another form
	 * at the current time boolean -> byte, and char -> short
	 */
	@Nullable
	Object get(String path);

	default NBTagView getTag(String path) {
		return this.getTag(path, null);
	}

	NBTagView getTag(String path, NBTagView def);

	/**
	 * @return unmodifiable equivalent to the vanilla method of getList
	 */
	default List<Object> getList(String path) {
		return this.get(path, NBTType.ANY_LIST, Collections.EMPTY_LIST);
	}

	/**
	 * equivalent to
	 * <code>
	 *     view.get(path, type, null)
	 * </code>
	 */
	@Nullable
	default <T> T get(String path, NBTType<T> type) {
		return this.get(path, type, null);
	}

	/**
	 * get an object with the exact type specified in the NBTType
	 * @param type the type of object to find
	 * @param def the default value to return
	 */
	<T> T get(String path, NBTType<T> type, T def);

	/**
	 * @return an unmodifiable iterator over the keys of this tag
	 */
	@Override
	Iterator<String> iterator();
}

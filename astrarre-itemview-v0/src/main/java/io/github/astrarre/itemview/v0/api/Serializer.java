package io.github.astrarre.itemview.v0.api;

import java.util.function.BiFunction;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.util.v0.api.Id;
import org.apache.logging.log4j.util.TriConsumer;

public interface Serializer<T> {
	Serializer<Boolean> BOOLEAN = of(NBTagView::getBool, NBTagView.Builder::putBool);
	Serializer<String> STRING = of(NBTagView::getString, NBTagView.Builder::putString);
	Serializer<Integer> INTEGER = of(NBTagView::getInt, NBTagView.Builder::putInt);
	Serializer<Float> FLOAT = of(NBTagView::getFloat, NBTagView.Builder::putFloat);
	Serializer<Double> DOUBLE = of(NBTagView::getDouble, NBTagView.Builder::putDouble);
	Serializer<Id> ID = of((tag, s) -> Id.create(tag.getString(s)), (tag, s, id) -> tag.putString(s, id.toString()));
	Serializer<NBTagView> NBT = of(NBTagView::getTag, NBTagView.Builder::putTag);

	static <T> Serializer<T> of(BiFunction<NBTagView, String, T> reader, TriConsumer<NBTagView.Builder, String, T> writer) {
		return new Serializer<T>() {
			@Override
			public T read(NBTagView input, String key) {
				return reader.apply(input, key);
			}

			@Override
			public void save(NBTagView.Builder output, String key, T instance) {
				writer.accept(output, key, instance);
			}
		};
	}

	static <T extends Serializable> Serializer<T> of(BiFunction<NBTagView, String, T> reader) {
		return of(reader, (builder, s, t) -> t.save(builder, s));
	}

	static <T extends Enum<T>> Serializer<T> ofEnum(Class<T> type) {
		T[] elements = type.getEnumConstants();
		return of((tagView, s) -> {
			int ordinal = tagView.getInt(s, 0);
			if(ordinal < elements.length) {
				return elements[ordinal];
			} else {
				return elements[0];
			}
		}, (tag, s, t) -> tag.putInt(s, t.ordinal()));
	}

	/**
	 * @param input the input tag
	 * @param key the key of the entry to read from
	 * @return a new instance read from the input
	 */
	T read(NBTagView input, String key);
	void save(NBTagView.Builder output, String key, T instance);
}

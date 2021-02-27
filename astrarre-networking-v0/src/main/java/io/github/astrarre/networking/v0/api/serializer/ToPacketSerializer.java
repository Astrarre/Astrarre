package io.github.astrarre.networking.v0.api.serializer;

import java.util.function.BiConsumer;
import java.util.function.Function;

import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.networking.v0.api.io.Output;

public interface ToPacketSerializer<T> {
	ToPacketSerializer<Boolean> BOOLEAN = of(Input::readBoolean, Output::writeBoolean);
	ToPacketSerializer<String> STRING = of(Input::readUTF, Output::writeUTF);
	ToPacketSerializer<Integer> INTEGER = of(Input::readInt, Output::writeInt);
	ToPacketSerializer<Float> FLOAT = of(Input::readFloat, Output::writeFloat);
	ToPacketSerializer<Double> DOUBLE = of(Input::readDouble, Output::writeDouble);


	static <T> ToPacketSerializer<T> of(Function<Input, T> reader, BiConsumer<Output, T> writer) {
		return new ToPacketSerializer<T>() {
			@Override
			public T read(Input input) {
				return reader.apply(input);
			}

			@Override
			public void write(Output output, T instance) {
				writer.accept(output, instance);
			}
		};
	}

	/**
	 * @return a new instance read from the input
	 */
	T read(Input input);
	void write(Output output, T instance);
}

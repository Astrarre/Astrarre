package io.github.astrarre.transfer.v0.api.access;

import io.github.astrarre.access.v0.api.func.Returns;
import io.github.astrarre.transfer.v0.api.Participant;

public interface ParticipantFunction<R, T> extends Returns<T> {
	T get(Participant<R> participant);

	default ParticipantFunction<R, T> andThen(ParticipantFunction<R, T> function) {
		return participant -> {
			T val = this.get(participant);
			if(val != null) return val;
			else return function.get(participant);
		};
	}
}

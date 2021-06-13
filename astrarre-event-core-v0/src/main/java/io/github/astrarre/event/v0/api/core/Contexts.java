package io.github.astrarre.event.v0.api.core;

import io.github.astrarre.event.internal.core.InternalContexts;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.func.Copier;

public class Contexts {
	/**
	 * States the context is copied when a BlockEvent is created, and applied when it is executed. (pistons use this)
	 */
	public static <T> void copyOnBlockEvent(ContextView<T> context, Copier<T> copier) {
		InternalContexts.SYNC.add(new InternalContexts.CopyEntry<>(context, copier));
	}

	/**
	 * States the context is copied when a scheduled tick is created, and applied when it is executed. (repeaters use this)
	 *
	 * chose either this or {@link #serializeWithScheduledTick(Id, ContextView, Serializer)}
	 */
	public static <T> void copyOnScheduledTick(ContextView<T> context, Copier<T> copier) {
		InternalContexts.COPY_SCHEDULED.add(new InternalContexts.CopyEntry<>(context, copier));
	}

	/**
	 * States the context is copied when a scheduled tick is created, and applied when it is executed. (repeaters use this)
	 */
	public static <T> void serializeWithScheduledTick(Id id, ContextView<T> context, Serializer<T> copier) {
		InternalContexts.SCHEDULED.put(id, new InternalContexts.SerializeEntry<>(context, copier));
	}
}

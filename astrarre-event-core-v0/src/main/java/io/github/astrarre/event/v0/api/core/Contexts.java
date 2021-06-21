package io.github.astrarre.event.v0.api.core;

import io.github.astrarre.event.internal.core.InternalContexts;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.func.Copier;

public class Contexts {
	/**
	 * @see #copyOnBlockEvent(ContextView, Copier)
	 * @see #copyOnScheduledTick(ContextView, Copier)
	 */
	public static <T> void copyForAll(ContextView<T> context, Copier<T> copier) {
		copyOnBlockEvent(context, copier);
		copyOnScheduledTick(context, copier);
		copyOnBlockEntityCreation(context, copier);
	}

	/**
	 * States the context is copied when a BlockEvent is created, and applied when it is executed. (pistons use this)
	 */
	public static <T> void copyOnBlockEvent(ContextView<T> context, Copier<T> copier) {
		InternalContexts.BLOCK_EVENT.add(new InternalContexts.CopyEntry<>(context, copier));
	}

	public static <T> void copyOnBlockEntityCreation(ContextView<T> context, Copier<T> copier) {
		InternalContexts.BLOCK_ENTITY_CREATE.add(new InternalContexts.CopyEntry<>(context, copier));
	}

	/**
	 * States the context is copied when a scheduled tick is created, and applied when it is executed. (repeaters use this)
	 */
	public static <T> void copyOnScheduledTick(ContextView<T> context, Copier<T> copier) {
		InternalContexts.COPY_SCHEDULED.add(new InternalContexts.CopyEntry<>(context, copier));
	}
}

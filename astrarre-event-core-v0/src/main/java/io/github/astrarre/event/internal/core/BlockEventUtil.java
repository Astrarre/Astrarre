package io.github.astrarre.event.internal.core;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.event.internal.core.access.ContextProvider;
import io.github.astrarre.event.v0.api.core.ContextView;

import net.minecraft.server.world.BlockEvent;

@SuppressWarnings ("unchecked")
public class BlockEventUtil {
	public static void initContext(BlockEvent event) {
		List<TempPair<InternalContexts.CopyEntry<?>, List<?>, List<?>>> eventData = new ArrayList<>();
		for (InternalContexts.CopyEntry<?> copy : InternalContexts.SYNC) {
			List<?> lst = extracted(copy);
			if (!lst.isEmpty()) {
				eventData.add(new TempPair<>(copy, lst));
			}
		}
		((ContextProvider) event).set(eventData);
	}

	static <T> List<T> extracted(InternalContexts.CopyEntry<T> copy) {
		List<T> lst = new ArrayList<>(copy.view().size());
		for (T o : copy.view()) {
			lst.add(copy.copier().copy(o));
		}
		return lst;
	}

	public static void loadContext(BlockEvent event) {
		var data = (List<TempPair<InternalContexts.CopyEntry<?>, List<?>, List<?>>>) ((ContextProvider) event).get();
		for (TempPair<InternalContexts.CopyEntry<?>, List<?>, List<?>> datum : data) {
			List<Object> replacement = new ArrayList<>();
			for (Object o : datum.b) {
				replacement.add(InternalContexts.put((ContextView) datum.a.put(), o));
			}
			datum.c = replacement;
		}
	}

	public static void pop(BlockEvent event) {
		var data = (List<TempPair<InternalContexts.CopyEntry<?>, List<?>, List<?>>>) ((ContextProvider) event).get();
		if (data != null) {
			for (TempPair<InternalContexts.CopyEntry<?>, List<?>, List<?>> datum : data) {
				List<?> c = datum.c;
				for (int i = c.size() - 1; i >= 0; i--) {
					Object o = c.get(i);
					InternalContexts.pop((ContextView) datum.a.view(), o);
				}
			}
		}
	}
}

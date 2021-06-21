package io.github.astrarre.event.internal.core;

import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.event.internal.core.access.ContextProvider;
import io.github.astrarre.event.v0.api.core.ContextView;

@SuppressWarnings ("unchecked")
public class CopyingContextUtil {
	public static void initContext(List<InternalContexts.CopyEntry<?>> sers, ContextProvider event) {
		List<TempPair<InternalContexts.CopyEntry<?>, List<?>, List<?>>> eventData = new ArrayList<>();
		for (InternalContexts.CopyEntry<?> copy : sers) {
			List<?> lst = extracted(copy);
			if (!lst.isEmpty()) {
				eventData.add(new TempPair<>(copy, lst));
			}
		}
		event.set(eventData);
	}

	static <T> List<T> extracted(InternalContexts.CopyEntry<T> copy) {
		List<T> lst = new ArrayList<>(copy.view().size());
		for (T o : copy.view()) {
			lst.add(copy.copier().copy(o));
		}
		return lst;
	}

	public static void loadContext(ContextProvider event) {
		var data = (List<TempPair<InternalContexts.CopyEntry<?>, List<?>, List<?>>>) event.get();
		if (data != null) {
			for (TempPair<InternalContexts.CopyEntry<?>, List<?>, List<?>> datum : data) {
				List<Object> replacement = new ArrayList<>();
				for (Object o : datum.b) {
					replacement.add(InternalContexts.put((ContextView) datum.a.view(), o));
				}
				datum.c = replacement;
			}
		}
	}

	public static void pop(ContextProvider event) {
		var data = (List<TempPair<InternalContexts.CopyEntry<?>, List<?>, List<?>>>) event.get();
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

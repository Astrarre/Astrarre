package io.github.astrarre.util.v0.fabric;

public class Tags { // todo port Tags
	/*public static <T> Set<T> get(Tag<T> tag) {
		Tag<T> root = findRoot(tag);
		if (root instanceof SetTagAccess) {
			return ((SetTagAccess) root).getValueSet();
		} else {
			return new AbstractSet<T>() {
				@Override
				public Iterator<T> iterator() {
					return root.values().iterator();
				}

				@Override
				public int size() {
					return root.values().size();
				}
			};
		}
	}

	public static <T> Tag<T> findRoot(Tag<T> root) {
		while (true) {
			if (root instanceof TagWrapperAccess) {
				root = ((TagWrapperAccess) root).callGet();
			} else if (FapiMixinPlugin.FAPI && root instanceof TagDelegateAccess) {
				root = ((TagDelegateAccess) root).callGetTag();
			} else {
				return root;
			}
		}
	}*/
}

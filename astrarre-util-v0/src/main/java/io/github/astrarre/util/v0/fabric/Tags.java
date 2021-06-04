package io.github.astrarre.util.v0.fabric;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import io.github.astrarre.util.internal.mixin.TagDelegateAccess;
import io.github.astrarre.util.internal.mixin.SetTagAccess;
import io.github.astrarre.util.internal.mixin.TagWrapperAccess;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.tag.Tag;

public class Tags {
	public static <T> Set<T> get(Tag<T> tag) {
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
			} else if (Validate.LOADER.isModLoaded("fabric-tag-extensions-v0") && root instanceof TagDelegateAccess) {
				root = ((TagDelegateAccess) root).callGetTag();
			} else {
				return root;
			}
		}
	}
}

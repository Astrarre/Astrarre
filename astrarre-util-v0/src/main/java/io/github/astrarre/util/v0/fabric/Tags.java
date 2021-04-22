package io.github.astrarre.util.v0.fabric;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import io.github.astrarre.util.internal.FapiMixinPlugin;
import io.github.astrarre.util.internal.fapimixin.TagDelegateAccess;
import io.github.astrarre.util.internal.mixin.SetTagAccess;
import io.github.astrarre.util.internal.mixin.TagWrapperAccess;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public class Tags {
	public static Set<Item> get(Tag<Item> tag) {
		Tag<Item> root = findRoot(tag);
		if (root instanceof SetTagAccess) {
			return ((SetTagAccess) root).getValueSet();
		} else {
			return new AbstractSet<Item>() {
				@Override
				public Iterator<Item> iterator() {
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
	}
}

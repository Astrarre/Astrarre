package io.github.astrarre.event.internal.core;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.github.astrarre.event.v0.api.core.ContextHolder;
import io.github.astrarre.event.v0.api.core.ContextView;
import io.github.astrarre.event.v0.api.core.SingleContextHolder;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.func.Copier;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InternalContexts {
	/**
	 * @see Block#scheduledTick(BlockState, ServerWorld, BlockPos, Random)
	 */
	public static final Map<Id, SerializeEntry<?>> SCHEDULED = new HashMap<>();

	/**
	 * @see Block#onSyncedBlockEvent(BlockState, World, BlockPos, int, int)
	 */
	public static final List<CopyEntry<?>> SYNC = new ArrayList<>();
	public static final List<CopyEntry<?>> COPY_SCHEDULED = new ArrayList<>();

	public record SerializeEntry<T>(ContextView<T> view, Serializer<T> serializer) {
	}

	public record CopyEntry<T>(ContextView<T> view, Copier<T> copier) {
	}

	public static <T> T put(ContextView<T> view, T val) {
		if(view instanceof SingleContextHolder) {
			return ((SingleContextHolder<T>) view).swap(val);
		} else if(view instanceof ContextHolder) {
			((ContextHolder<T>) view).push(val);
			return val;
		} else {
			throw new IllegalStateException("Unknown class!");
		}
	}

	public static <T> void pop(ContextView<T> view, T val) {
		if(view instanceof SingleContextHolder) {
			((SingleContextHolder<T>) view).swap(val);
		} else if(view instanceof ContextHolder) {
			((ContextHolder<T>) view).pop(val);
		} else {
			throw new IllegalStateException("Unknown class!");
		}
	}
}

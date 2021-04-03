package io.github.astrarre.recipes.v0.api.recipe;

import java.util.List;

import io.github.astrarre.recipes.v0.api.RecipePart;
import org.jetbrains.annotations.Nullable;

public final class Result {
	private final boolean failed;
	private final int failedIndex;
	@Nullable
	private final List<?> parts;

	public Result(boolean failed, int index, @Nullable List<?> parts) {
		this.failed = failed;
		this.failedIndex = index;
		this.parts = parts;
	}

	/**
	 * @return true if the recipe failed
	 */
	public boolean isFailed() {
		return this.failed;
	}

	public boolean isSuccess() {
		return !this.failed;
	}

	/**
	 * @return -1 if the recipe did not fail, or the index of the part that was invalid
	 */
	public int getFailedIndex() {
		return this.failedIndex;
	}

	/**
	 * @return the input value for the 'most complete' recipe
	 */
	@Nullable
	public <V> V getInput(RecipePart<V, ?> parser, int index) {
		if(this.parts == null) return null;
		return (V) this.parts.get(index);
	}
}

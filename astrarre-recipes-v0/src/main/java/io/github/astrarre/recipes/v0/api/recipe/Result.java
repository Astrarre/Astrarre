package io.github.astrarre.recipes.v0.api.recipe;

public final class Result {
	private final boolean failed;
	private final int failedIndex;

	public Result(boolean failed, int index) {
		this.failed = failed;
		this.failedIndex = index;
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
}

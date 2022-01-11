package io.github.astrarre.recipe.v0.api;

import net.minecraft.util.Identifier;

public interface ResourceIdentifiable {
	/**
	 * @param id the id of the resource that was used to generate this object
	 */
	void setResourceId(Identifier id);
}

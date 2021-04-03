package io.github.astrarre.recipes.v0.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.astrarre.recipes.internal.recipe.RecipeImpl;
import io.github.astrarre.recipes.internal.recipe.RecipeParser;
import io.github.astrarre.recipes.v0.api.recipe.Recipe;
import io.github.astrarre.util.v0.api.Id;
import io.github.astrarre.util.v0.api.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

/**
 * a single file can choose between multiple recipes this file will load recipes. Keep in mind a that this does not check if 2 recipes will conflict in parsing
 *
 * recipes would go in 'data/modid/mcrf/path/*' eg. 'data/test/mcrf/test_machine/defaults.mcrf'
 */
public class RecipeFile implements SimpleSynchronousResourceReloadListener {
	private final List<RecipeImpl> recipes = new ArrayList<>();
	private final Identifier fileId;
	private final Logger logger;

	public RecipeFile(String modid, String name) {
		this(new Identifier(modid, name));
	}

	public RecipeFile(Identifier id) {
		this.fileId = id;
		this.logger = LogManager.getLogger("mcrf/" + id.getNamespace() + "/" + id.getPath());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(this);
	}

	public RecipeFile(Id id) {
		this(id.to());
	}

	public RecipeFile add(Recipe recipe) {
		this.recipes.add((RecipeImpl) recipe);
		return this;
	}

	@Override
	public Identifier getFabricId() {
		return this.fileId;
	}

	@Override
	public void apply(ResourceManager manager) {
		this.logger.info("clearing recipes...");
		for (RecipeImpl recipe : this.recipes) {
			recipe.values.clear();
		}

		RecipeParser parser = new RecipeParser(this.recipes);
		for (Identifier resource : manager.findResources("mcrf/" + this.fileId.getPath(), s -> s.endsWith(".mcrf"))) {
			if (!this.fileId.getNamespace().equals(resource.getNamespace())) {
				continue;
			}

			this.logger.info("loading " + resource);

			try {
				Resource rss = manager.getResource(resource);
				parser.parseToCompletion(rss.getInputStream(), resource.toString());
			} catch (IOException e) {
				Validate.rethrow(e);
			}
		}

		for (RecipeImpl recipe : this.recipes) {
			recipe.onReload();
		}
	}
}

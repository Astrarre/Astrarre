package io.github.astrarre.rendering.v1.edge.glslTransformer;

import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Lifecycle;
import io.github.astrarre.rendering.v1.edge.glslTransformer.inject.InjectTransform;
import io.github.astrarre.rendering.v1.edge.glslTransformer.inject.ReplaceTransform;
import io.github.astrarre.rendering.v1.edge.glslTransformer.inject.Transform;
import io.github.astrarre.rendering.v1.edge.glslTransformer.search.ConstrainedSearch;
import io.github.astrarre.rendering.v1.edge.glslTransformer.search.LineBeforeSearch;
import io.github.astrarre.rendering.v1.edge.glslTransformer.search.MethodDeclarationSearch;
import io.github.astrarre.rendering.v1.edge.glslTransformer.search.MethodInvokeSearch;
import io.github.astrarre.rendering.v1.edge.glslTransformer.search.MethodPrototypeSearch;
import io.github.astrarre.rendering.v1.edge.glslTransformer.search.OrdinalSearch;
import io.github.astrarre.rendering.v1.edge.glslTransformer.search.Search;
import io.github.astrarre.util.v0.api.Validate;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public class GlslTransformer {
	public static final Registry<Factory<Search>> SEARCH;
	public static final Registry<Factory<Transform>> TRANSFORM;

	static {
		Function<String, Identifier> id = s -> new Identifier("astrarre", s);
		var key = RegistryKey.<Factory<Search>>ofRegistry(id.apply("glsl-search"));
		SEARCH = new SimpleRegistry<>(key, Lifecycle.stable());
		var key2 = RegistryKey.<Factory<Transform>>ofRegistry(id.apply("glsl-transformers"));
		TRANSFORM = new SimpleRegistry<>(key2, Lifecycle.stable());

		Registry.register(SEARCH, id.apply("constrained"), object -> {
			JsonObject input = JsonHelper.asObject(object, "constraint parameters");
			Search from = fromObject(JsonHelper.getObject(input, "from"));
			Search search = fromObject(JsonHelper.getObject(input, "search"));
			Search to;
			if(input.has("len")) {
				to = fromObject(JsonHelper.getObject(input, "len"));
			} else {
				to = null;
			}
			return new ConstrainedSearch(search, from, to);
		});

		Registry.register(SEARCH, id.apply("methodInvoke"), input -> new MethodInvokeSearch(JsonHelper.asString(input, "method name")));
		Registry.register(SEARCH, id.apply("methodDeclare"), input -> new MethodDeclarationSearch(JsonHelper.asString(input, "method name")));
		Registry.register(SEARCH, id.apply("methodPrototype"), input -> new MethodPrototypeSearch(JsonHelper.asString(input, "method name")));
		Registry.register(SEARCH, id.apply("ordinal"), object -> {
			JsonObject input = JsonHelper.asObject(object, "ordinal parameters");
			Search search = fromObject(input);
			int ordinal = JsonHelper.getInt(input, "ordinal");
			return new OrdinalSearch(search, ordinal);
		});
		Registry.register(SEARCH, id.apply("line_before"), input -> new LineBeforeSearch(fromObject(JsonHelper.asObject(input, "line before input"))));
		Registry.register(SEARCH, id.apply("line"), input -> Search.line(JsonHelper.asInt(input, "line number")));

		Registry.register(TRANSFORM, id.apply("replace"), input -> {
			JsonObject object = JsonHelper.asObject(input, "replace parameters");
			Search search = fromObject(object);
			String replace = JsonHelper.getString(object, "replace");
			return new ReplaceTransform(search, replace);
		});

		Registry.register(TRANSFORM, id.apply("inject"), input -> {
			JsonObject object = JsonHelper.asObject(input, "inject parameters");
			Search search = fromObject(object);
			String replace = JsonHelper.getString(object, "inject");
			return new InjectTransform(search, replace);
		});
	}

	static Search fromObject(JsonObject object) {
		var str = new Identifier(JsonHelper.getString(object, "id"));
		var input = object.get("value");
		return Validate.notNull(SEARCH.get(str), str + "").create(input);
	}

	public interface Factory<T> {
		T create(JsonElement input);
	}
}

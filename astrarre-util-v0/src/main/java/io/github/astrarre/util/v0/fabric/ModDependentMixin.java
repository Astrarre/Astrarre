package io.github.astrarre.util.v0.fabric;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;

public class ModDependentMixin implements IMixinConfigPlugin {
	private static final String MIXIN_IF_PRESENT = Type.getDescriptor(ModEnvironment.class);
	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		String resourceName = mixinClassName.replace('.', '/') + ".class";
		InputStream stream = ModDependentMixin.class.getResourceAsStream(resourceName);
		if(stream == null) {
			stream = ModDependentMixin.class.getResourceAsStream("/" + resourceName);
		}
		if(stream == null) {
			throw new IllegalArgumentException("Unable to find mixin with name " + mixinClassName);
		}
		try {
			ClassReader reader = new ClassReader(stream);
			ClassNode node = new ClassNode();
			reader.accept(node, ClassReader.SKIP_FRAMES | ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
			if(node.invisibleAnnotations == null) return true;
			for (AnnotationNode annotation : node.invisibleAnnotations) {
				if(MIXIN_IF_PRESENT.equals(annotation.desc)) {
					String modid = (String) annotation.values.get(1);
					return FabricLoader.getInstance().isModLoaded(modid);
				}
			}
			return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}

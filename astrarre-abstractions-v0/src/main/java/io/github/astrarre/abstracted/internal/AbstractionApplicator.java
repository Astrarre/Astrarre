package io.github.astrarre.abstracted.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.VarInsnNode;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.loader.api.ModContainer;

public class AbstractionApplicator implements Runnable, Opcodes {
	private static final Logger LOGGER = LogManager.getLogger("Abstraction Applicator");

	private static boolean validateParameters(Type[] parameters, InsnList list) {
		for (int i = 0; i < parameters.length; i++) {
			int fi = i;
			if (!ifInstanceOf(list.get(i + 2), VarInsnNode.class, insn -> insn.var == fi + 1)) {
				return false;
			}
		}
		return true;
	}

	private static <T> boolean ifInstanceOf(Object object, Class<T> cls, Predicate<T> type) {
		if (cls.isInstance(object)) {
			return type.test((T) object);
		}
		return false;
	}

	@Override
	public void run() {
		FabricLoader loader = FabricLoader.getInstance();
		MappingResolver resolver = loader.getMappingResolver();
		boolean isNamed = resolver.getCurrentRuntimeNamespace().equals("named");
		try {
			for (ModContainer mod : loader.getAllMods()) {
				Path dir = mod.getPath("astrarre_manifest");
				if (Files.isDirectory(dir) && Files.exists(dir)) {
					LOGGER.info("Applying abstraction for " + mod.getMetadata().getId());
					read(Files.newInputStream(dir.resolve("interface.properties"))).forEach((a, b) -> InterfaceAbstractionApplicator.apply(resolver.mapClassName("intermediary", ((String) a).replace('/', '.')).replace('.', '/'), (String) b, isNamed));
					read(Files.newInputStream(dir.resolve("base_impl.properties"))).forEach((a, b) -> BaseAbstractionApplicator.apply((String) a, resolver.mapClassName("intermediary", ((String) b).replace('/', '.')).replace('.', '/'), isNamed));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Properties read(InputStream reader) {
		Properties properties = new Properties();
		try {
			properties.load(reader);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return properties;
	}
}

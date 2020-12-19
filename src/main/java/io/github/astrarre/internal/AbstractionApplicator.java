package io.github.astrarre.internal;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Properties;
import java.util.function.Predicate;

import com.chocohead.mm.api.ClassTinkerers;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.fabricmc.loader.api.FabricLoader;

public class AbstractionApplicator implements Runnable {
	public static final Properties INTERFACE_PROPERTIES;
	public static final Properties BASE_PROPERTIES;

	static {
		INTERFACE_PROPERTIES = read(AbstractionApplicator.class.getResourceAsStream("/manifest.properties"));
		BASE_PROPERTIES = read(AbstractionApplicator.class.getResourceAsStream("/base_manifest.properties"));
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
		boolean isNamed = FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace().equals("named");
		INTERFACE_PROPERTIES.forEach((k, v) -> {
			String className = (String) k;
			// sometimes default methods will conflict and crash the game, this is a hack patch to allow the game to
			// launch in the developer environment
			// when amalgamation is finished we'll probably be able to remove this
			if (isNamed) {
				ClassTinkerers.addTransformation((String) v, AbstractionApplicator::stripConflicts);
			}

			ClassTinkerers.addTransformation(className, c -> c.interfaces.add((String) v));
		});

		if (isNamed) {
			BASE_PROPERTIES.forEach((k, v) -> ClassTinkerers.addTransformation((String) v, AbstractionApplicator::stripConflicts));
		}
	}

	private static void stripConflicts(ClassNode node) {
		Iterator<MethodNode> iterator = node.methods.iterator();
		while (iterator.hasNext()) {
			MethodNode method = iterator.next();
			if (!Modifier.isStatic(method.access) && method.invisibleAnnotations != null) {
				for (AnnotationNode annotation : method.invisibleAnnotations) {
					if (annotation.desc.equals("Lio/github/astrarre/abstracter/ConflictingDefault;")) {
						iterator.remove();
						break;
					}
				}
			}
		}
	}
}

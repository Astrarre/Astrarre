package io.github.astrarre.base;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Properties;
import java.util.function.Predicate;

import com.chocohead.mm.api.ClassTinkerers;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.loader.api.ModContainer;

public class AbstractionApplicator implements Runnable, Opcodes {
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
				Path path = mod.getPath("/intr_manifest.properties");
				if(Files.exists(path)) {
					Properties interfaceProperties = read(Files.newInputStream(path));
					interfaceProperties.forEach((k, v) -> {
						String className = resolver.mapClassName("intermediary", ((String) k).replace('/', '.'));
						System.out.println("transforming " + className);
						if (isNamed) {
							ClassTinkerers.addTransformation((String) v, c -> stripConflicts(c, true));
						}

						ClassTinkerers.addTransformation(className, c -> c.interfaces.add((String) v));
					});
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}


		//if (isNamed) {
		//	BASE_PROPERTIES.forEach((k, v) -> ClassTinkerers.addTransformation((String) v, c -> stripConflicts(c, false)));
		//}
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

	// strip failing override methods
	private static void stripConflicts(ClassNode node, boolean iface) {
		Iterator<MethodNode> iterator = node.methods.iterator();
		while (iterator.hasNext()) {
			MethodNode method = iterator.next();
			if (!Modifier.isStatic(method.access)) {
				// if final method, or interface
				if (Modifier.isFinal(method.access) || iface) {
					InsnList list = method.instructions;
					if (list != null && list.size() > 0) {
						ListIterator<AbstractInsnNode> iter = list.iterator();
						int current = 0;
						while (iter.hasNext()) {
							AbstractInsnNode n = iter.next();
							if (n instanceof VarInsnNode) {
								if (((VarInsnNode) n).var == current) {
									current++;
								} else {
									break;
								}
							}
						}

						if (iter.hasNext()) {
							AbstractInsnNode next = iter.next();
							if (next instanceof MethodInsnNode) {
								// if interface (invokevirtual/interface) if base (invokespecial)
								MethodInsnNode methodInvoke = (MethodInsnNode) next;
								int opcode = methodInvoke.getOpcode();
								if ((iface && (opcode == INVOKEVIRTUAL || opcode == INVOKEINTERFACE)) || (!iface && (opcode == INVOKESPECIAL))) {
									if (methodInvoke.name.equals(method.name) && methodInvoke.desc.equals(method.desc)) {
										if (iter.hasNext()) {
											AbstractInsnNode returnInsn = iter.next();
											if (returnInsn instanceof InsnNode && !iter.hasNext()) {
												iterator.remove();
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}

package io.github.astrarre.internal;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.function.Predicate;

import com.chocohead.mm.api.ClassTinkerers;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.fabricmc.loader.api.FabricLoader;

public class AbstractionApplicator implements Runnable {
	public static final Properties PROPERTIES;

	static {
		InputStream stream = AbstractionApplicator.class.getResourceAsStream("/manifest.properties");
		PROPERTIES = read(stream);
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

	@Override
	public void run() {
		boolean isNamed = FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace().equals("named");
		PROPERTIES.forEach((k, v) -> {
			String className = (String) k;
			// sometimes default methods will conflict and crash the game, this is a hack patch to allow the game to
			// launch in the developer environment
			// when amalgamation is finished we'll probably be able to remove this
			if (isNamed) {
				ClassTinkerers.addTransformation((String) v, c -> {
					for (MethodNode method : c.methods) {
						if (!Modifier.isStatic(method.access)) {
							// todo just add some marker or something
							Type methodType = Type.getMethodType(method.desc);
							Type[] parameters = methodType.getArgumentTypes();
							InsnList list = method.instructions;
							int expectedInstructions = 0;
							expectedInstructions += 2; // aload this, checkcast this -> mc
							expectedInstructions += parameters.length; // aload 1->X
							expectedInstructions += 2; // invokeVirtual, return
							if (list.size() == expectedInstructions &&
							    // aload 0
							    ifInstanceOf(list.get(0), VarInsnNode.class, i -> i.var == 0) &&
							    // checkcast nms
							    ifInstanceOf(list.get(1),
									    TypeInsnNode.class,
									    i -> i.getOpcode() == Opcodes.CHECKCAST) &&
							    // validate parameters (aload 1 -> x)
							    validateParameters(parameters, list) &&
							    ifInstanceOf(list.get(2 + parameters.length), MethodInsnNode.class,
									i -> (i.getOpcode() == Opcodes.INVOKEVIRTUAL || i.getOpcode() == Opcodes.INVOKEINTERFACE) && i.name.equals(
											method.name) && i.desc.equals(method.desc))) {
								method.instructions.clear();
								method.access |= Opcodes.ACC_ABSTRACT;
							}
						}
					}
				});
			}

			// todo remap target and manifest
			ClassTinkerers.addTransformation(className, c -> c.interfaces.add((String) v));
		});
	}

	private static <T> boolean ifInstanceOf(Object object, Class<T> cls, Predicate<T> type) {
		if (cls.isInstance(object)) {
			return type.test((T) object);
		}
		return false;
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
}

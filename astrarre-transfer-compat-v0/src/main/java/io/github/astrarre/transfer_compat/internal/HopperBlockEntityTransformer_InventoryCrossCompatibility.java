package io.github.astrarre.transfer_compat.internal;

import java.util.List;
import java.util.Set;

import io.github.astrarre.util.v0.api.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

/**
 * This transformer
 *  1) clones the HopperEntity#getInventoryAt and gives it the name HopperBlockEntity#astrarre_copied_getInventoryAt
 *  2) adds astrarre's hooks to allow any vanilla/external invocation of getInventoryAt to maintain compatibility with astrarre's transfer api
 *
 * This allows astrarre to be compatible with any mixins into HopperBlockEntity#getInventoryAt while preventing infinite loops
 *
 * where is your god now
 */
public class HopperBlockEntityTransformer_InventoryCrossCompatibility implements IMixinConfigPlugin, Opcodes {
	private static final Logger LOGGER = LogManager.getLogger("HopperBlockEntityTransformer");
	private static final MappingResolver RESOLVER = FabricLoader.getInstance().getMappingResolver();
	/**
	 * can't reference class directly or it'll get class loaded
	 * @see HopperBlockEntityTransformer_InventoryCrossCompatibilityHooks
	 */
	private static final String HOOKS = Type.getInternalName(HopperBlockEntityTransformer_InventoryCrossCompatibility.class) + "Hooks";
	private static final String WORLD = remap("net.minecraft.class_1937");
	private static final String INVENTORY = remap("net.minecraft.class_1263");

	private static final String DESC = String.format("(L%s;DDD)L%s;", WORLD, INVENTORY);
	private static final String NAME = RESOLVER.mapMethodName("intermediary", "net.minecraft.class_2614", "method_11251", "(Lnet/minecraft/class_1937;DDD)Lnet/minecraft/class_1263;");

	private static final Type BLOCK_STATE = Type.getObjectType(remap("net.minecraft.class_2680"));
	private static final Type BLOCK_ENTITY = Type.getObjectType(remap("net.minecraft.class_2586"));
	private static final Type BLOCK_POS = Type.getObjectType(remap("net.minecraft.class_2338"));
	private static final Type INV_TYPE = Type.getObjectType(INVENTORY);
	private static final Type WORLD_TYPE = Type.getObjectType(WORLD);

	private static String remap(String name) {
		return RESOLVER.mapClassName("intermediary", name).replace('.', '/');
	}

	// @formatter:off
	@Override
	public void onLoad(String mixinPackage) {}
	@Override public String getRefMapperConfig() {return null;}
	@Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {return true;}
	@Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
	@Override public List<String> getMixins() {return null;}
	// @formatter:on
	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		LOGGER.info("Transforming " + targetClassName);
		MethodNode toAdd = null;
		for (MethodNode method : targetClass.methods) {
			if (NAME.equals(method.name) && DESC.equals(method.desc)) {
				LOGGER.info("Found target!");
				MethodNode clone = new MethodNode(method.access, "astrarre_copied_getInventoryAt", method.desc, method.signature, null);
				clone.exceptions = method.exceptions;
				method.accept(clone);
				LOGGER.info("cloned target!");

				try {
					InsnList instructions = method.instructions;
					SourceInterpreter interpreter = new SourceInterpreter();
					Analyzer<SourceValue> analyzer = new Analyzer<>(interpreter);
					Frame<SourceValue>[] frames = analyzer.analyze(targetClass.name, method);
					int blockStateInjectionPoint = -1, blockEntityInjectionPoint = -1;
					for (int insn = 0; insn < frames.length; insn++) {
						Frame<SourceValue> frame = frames[insn];
						if(frame == null) {
							continue;
						}
						int worldIndex = -1, posIndex = -1, stateIndex = -1, blockEntityIndex = -1, inventoryIndex = -1;
						for (int i = 0; i < frame.getLocals(); i++) {
							SourceValue value = frame.getLocal(i);
							if(value == null) {
								continue;
							}

							Type type = estimate(method, instructions, frames, value);

							if (inventoryIndex == -1 && INV_TYPE.equals(type)) {
								inventoryIndex = i;
							} else if (blockEntityIndex == -1 && BLOCK_ENTITY.equals(type)) {
								blockEntityIndex = i;
							} else if (posIndex == -1 && BLOCK_POS.equals(type)) {
								posIndex = i;
							} else if (stateIndex == -1 && BLOCK_STATE.equals(type)) {
								stateIndex = i;
							} else if (worldIndex == -1 && WORLD_TYPE.equals(type)) {
								worldIndex = i;
							}
						}

						if(stateIndex != -1 && inventoryIndex == -1) {
							inventoryIndex = 7;
						}

						if (inventoryIndex != -1 && stateIndex != -1 && worldIndex == -1) {
							worldIndex = 0;
						}

						if (worldIndex != -1 && posIndex != -1 && stateIndex != -1 && inventoryIndex != -1) {
							if (blockStateInjectionPoint == -1) {
								LOGGER.info("Found BlockState Injection Point");
								blockStateInjectionPoint = insn;
							}

							if (blockEntityIndex != -1) {
								LOGGER.info("Found BlockEntity Injection Point");
								blockEntityInjectionPoint = insn;
								break;
							}
						}
					}

					if(blockStateInjectionPoint == -1) {
						throw new IllegalStateException("Unable to find BlockState Injection point!");
					} else if(blockEntityInjectionPoint == -1) {
						throw new IllegalStateException("Unable to find BlockEntity Injection point!");
					}

					extracted(method, instructions, frames, blockEntityInjectionPoint, true);
					extracted(method, instructions, frames, blockStateInjectionPoint, false);
				} catch (AnalyzerException e) {
					throw Validate.rethrow(e);
				}
				toAdd = clone;
				break;
			}
		}

		if(toAdd == null) {
			throw new IllegalStateException("Did not find injection target!");
		}
		targetClass.methods.add(toAdd);
	}

	private static void extracted(MethodNode method, InsnList instructions, Frame<SourceValue>[] frames, int injectPoint, boolean blockEntity) {
		InsnList list = method.instructions;
		InsnList injection = new InsnList();
		Frame<SourceValue> stateFrame = frames[injectPoint];
		injection.add(new VarInsnNode(ALOAD, local(method, instructions, frames, stateFrame, BLOCK_POS)));
		injection.add(new VarInsnNode(ALOAD, local(method, instructions, frames, stateFrame, WORLD_TYPE, 0)));
		injection.add(new VarInsnNode(ALOAD, local(method, instructions, frames, stateFrame, BLOCK_STATE)));
		if(blockEntity) {
			injection.add(new VarInsnNode(ALOAD, local(method, instructions, frames, stateFrame, BLOCK_ENTITY)));
		}

		String desc;
		if(blockEntity) {
			desc = String.format("(%s%s%s%s)%s", BLOCK_POS, WORLD_TYPE, BLOCK_STATE, BLOCK_ENTITY, INV_TYPE);
		} else {
			desc = String.format("(%s%s%s)%s", BLOCK_POS, WORLD_TYPE, BLOCK_STATE, INV_TYPE);
		}
		injection.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "get", desc));
		injection.add(new InsnNode(DUP));
		int inv = estimate(method, instructions, injectPoint, INV_TYPE);
		injection.add(new VarInsnNode(ASTORE, inv));
		LabelNode node = new LabelNode(new Label());
		injection.add(new JumpInsnNode(IFNULL, node));
		injection.add(new VarInsnNode(ALOAD, inv));
		injection.add(new InsnNode(ARETURN));
		injection.add(node);

		instructions.insert(list.get(injectPoint), injection);
	}

	@Nullable
	protected static Type estimate(MethodNode method, InsnList list, Frame<SourceValue>[] frame, SourceValue value) {
		for (AbstractInsnNode node : value.insns) {
			Type type = estimate(method, list, frame, node);
			if(type != null) {
				return type;
			}
		}
		return null;
	}

	private static Type estimate(MethodNode method, InsnList list, Frame<SourceValue>[] frames, AbstractInsnNode node) {
		if(node instanceof MethodInsnNode) {
			MethodInsnNode n = (MethodInsnNode) node;
			if(n.name.equals("<init>")) {
				Type type = Type.getMethodType(n.desc);
				return estimate(method, list, frames, list.get(list.indexOf(node) - type.getArgumentTypes().length - 2));
			}

			return Type.getMethodType(((MethodInsnNode) node).desc).getReturnType();
		} else if(node instanceof VarInsnNode) {
			VarInsnNode var = (VarInsnNode) node;
			Type type = estimate(method, list, node, var.var);

			if(type == null) {
				if (var.getOpcode() == ASTORE) {
					type = estimate(method, list, frames, var.getPrevious());
				} else if (var.getOpcode() == ALOAD) {
					int v = var.var;
					Frame<SourceValue> at = frames[list.indexOf(node)];
					SourceValue value = at.getLocal(v);
					type = estimate(method, list, frames, value);
				}
			}

			return type;
		} else if(node instanceof TypeInsnNode) {
			String s = ((TypeInsnNode) node).desc;
			if(s.length() < 3 || s.endsWith(";")) {
				return Type.getType(s);
			} else {
				return Type.getObjectType(s);
			}
		} else if(node instanceof FieldInsnNode) {
			String s = ((FieldInsnNode) node).desc;
			if(s.length() < 3 || s.endsWith(";")) {
				return Type.getType(s);
			} else {
				return Type.getObjectType(s);
			}
		}
		return null;
	}

	private static int local(MethodNode method,InsnList list, Frame<SourceValue>[] frames, Frame<SourceValue> valueFrame, Type type) {
		return local(method, list, frames, valueFrame, type, -1);
	}
	private static int local(MethodNode method, InsnList list, Frame<SourceValue>[] frames, Frame<SourceValue> valueFrame, Type type, int defaut) {
		for (int i = 0; i < valueFrame.getLocals(); i++) {
			SourceValue value = valueFrame.getLocal(i);
			if(value != null && type.equals(estimate(method, list, frames, value))) {
				return i;
			}
		}
		return defaut;
	}

	private static int estimate(MethodNode method, InsnList list, int index, Type type) {
		for (LocalVariableNode variable : method.localVariables) {
			if(list.indexOf(variable.start) < index && list.indexOf(variable.end) > index && variable.desc.equals(type.getDescriptor())) {
				return variable.index;
			}
		}
		return -1;
	}

	private static Type estimate(MethodNode method, InsnList list, AbstractInsnNode node, int local) {
		int nodeIndex = list.indexOf(node);
		for (LocalVariableNode variable : method.localVariables) {
			if(variable.index == local && list.indexOf(variable.start) < nodeIndex && list.indexOf(variable.end) < nodeIndex) {
				return Type.getType(variable.desc);
			}
		}
		return null;
	}
}

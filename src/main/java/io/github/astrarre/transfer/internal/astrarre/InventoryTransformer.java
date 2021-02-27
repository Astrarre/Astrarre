package io.github.astrarre.transfer.internal.astrarre;

import java.util.List;
import java.util.Set;

import io.github.astrarre.util.v0.api.Validate;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

/**
 * this transformer adds a new HopperBlockEntity#getInventoryAt method that does not call Astrarre's Access' and adds some extra parameters for optimization
 * and then injects Astrarre's Access' calls into the old method.
 *
 * This allows astrarre to be compatible with any mixins into HopperBlockEntity#getInventoryAt while preventing infinite loops
 *
 * todo do this at some point
 */
public class InventoryTransformer implements IMixinConfigPlugin, Opcodes {
	private static final MappingResolver RESOLVER = FabricLoader.getInstance().getMappingResolver();
	/**
	 * can't reference class directly or it'll get class loaded
	 */
	private static final String HOOKS = Type.getInternalName(InventoryTransformer.class) + "Hooks";
	private static final String WORLD = RESOLVER.mapClassName("intermediary", "net.minecraft.class_1937");
	private static final String INVENTORY = RESOLVER.mapClassName("intermediary", "net.minecraft.class_1263");

	private static final String DESC = String.format("(L%s;DDD)L%s;", WORLD.replace('.', '/'), INVENTORY.replace('.', '/'));
	private static final String NAME = FabricLoader.getInstance().getMappingResolver()
			                                   .mapMethodName("intermediary", "net.minecraft.class_2614", "method_11251", DESC);


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
		if(true) return; // todo disabled until I get around to it

		MethodNode toAdd = null;
		for (MethodNode method : targetClass.methods) {
			if(NAME.equals(method.name) && DESC.equals(method.desc)) {
				MethodNode clone = new MethodNode(method.access, "astrarre_copied_getInventoryAt", method.desc, method.signature, null);
				clone.exceptions = method.exceptions;
				clone.accept(method);

				BasicInterpreter interpreter = new BasicInterpreter();
				Analyzer<BasicValue> analyzer = new Analyzer<>(interpreter);
				try {
					Frame<BasicValue>[] frames = analyzer.analyze(targetClass.name, method);
					for (Frame<BasicValue> frame : frames) {
						int worldIndex = -1, posIndex = -1, stateIndex = -1, blockEntityIndex = -1;
						for (int i = 0; i < frame.getLocals(); i++) {

						}
					}
				} catch (AnalyzerException e) {
					throw Validate.rethrow(e);
				}
				toAdd = clone;
				break;
			}
		}
		targetClass.methods.add(toAdd);
	}
}

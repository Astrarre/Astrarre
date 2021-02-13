package cls;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.abs.InterfaceAbstracter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

import net.minecraft.item.ItemStack;

public class ItemStackAbs extends InterfaceAbstracter {
	public static final String ITEM_KEY = "io/github/astrarre/itemview/v0/api/item/ItemKey";

	public ItemStackAbs() {
		super(ItemStack.class);
		this.post(this::process);
	}

	private void process(AbstracterConfig config, Class<?> cls, ClassNode node, boolean impl) {
		MethodVisitor visitor = node.visitMethod(ACC_PUBLIC | (impl ? 0 : ACC_ABSTRACT), "getOrCreateKey", "()L" + ITEM_KEY + ";", null, null);
		if(impl) {
			visitor.visitVarInsn(ALOAD, 0);
			visitor.visitTypeInsn(CHECKCAST, this.cls);
			visitor.visitMethodInsn(INVOKESTATIC, ITEM_KEY, "of", "(L" + this.cls + ";)L" + ITEM_KEY + ";", false);
			visitor.visitInsn(ARETURN);
		}
	}
}

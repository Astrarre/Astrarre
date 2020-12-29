import java.io.IOException;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.AbstracterUtil;
import io.github.astrarre.abstracter.abs.InterfaceAbstracter;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Main {
	public static void main(String[] args) throws IOException {
		AbstracterUtil.applyParallel(args[0], () -> {
			AbstracterUtil.registerDefaultInterface(Item.class);
			AbstracterUtil.registerDefaultInterface(ItemStack.class);
			AbstracterConfig.registerInterface(new InterfaceAbstracter(ItemStack.class).post((aClass, node, b) -> {
				node.interfaces.add("io/github/astrarre/itemview/v0/api/item/ItemStackView");
			}));
		});
	}
}

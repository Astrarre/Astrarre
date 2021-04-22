package io.github.astrarre.transfer.v0.api.ad;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class AstrarreTransferTooltippedItem extends Item {
	public AstrarreTransferTooltippedItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(new TranslatableText("tooltip.astrarre-transfer-v0.uses_tooltip").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
	}
}

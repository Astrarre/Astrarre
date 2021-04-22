package io.github.astrarre.transfer.v0.api.ad;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

/**
 * A block item with a tooltip stating the item uses the Astrarre Transfer API so the user knows what mods are compatible
 */
public class AstrarreTransferTooltippedBlockItem extends BlockItem {
	public AstrarreTransferTooltippedBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(new TranslatableText("tooltip.astrarre-transfer-v0.uses_tooltip").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
	}
}

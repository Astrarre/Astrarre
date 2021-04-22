package testmod;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.access.v0.api.provider.Provider;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.ad.AstrarreTransferTooltippedItem;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;

public class TestModMain implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("testmod", "test_item"), new TestItem());
	}

	public static class TestItem extends AstrarreTransferTooltippedItem implements Provider {
		public TestItem() {
			super(new Settings());
		}

		@Override
		public ActionResult useOnBlock(ItemUsageContext context) {
			World world = context.getWorld();
			if(!world.isClient) {
				BlockPos pos = context.getBlockPos();
				Participant<Fluid> participant = FabricParticipants.FLUID_WORLD.get().get(null, world, pos);
				assert participant != null;
				try(Transaction transaction = Transaction.create(true)) {
					participant.insert(transaction, Fluids.WATER, Droplet.BOTTLE);
					participant.insert(transaction, Fluids.WATER, Droplet.BOTTLE);
					participant.insert(transaction, Fluids.WATER, Droplet.BOTTLE);
				}
				return ActionResult.CONSUME;
			}
			return super.useOnBlock(context);
		}

		@Override
		public @Nullable Object get(Access<?> access) {
			return null;
		}
	}
}

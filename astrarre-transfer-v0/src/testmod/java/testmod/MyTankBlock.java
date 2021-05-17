package testmod;

import io.github.astrarre.access.v0.api.Access;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.v0.api.Droplet;
import io.github.astrarre.transfer.v0.api.Participant;
import io.github.astrarre.transfer.v0.api.participants.FixedObjectVolume;
import io.github.astrarre.transfer.v0.api.player.PlayerParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.transfer.v0.fabric.participants.FabricParticipants;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class MyTankBlock extends Block implements BlockEntityProvider {
	public MyTankBlock(Settings settings) {
		super(settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new Tile();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack inHand = player.getStackInHand(hand);
		PlayerParticipant participant = FabricParticipants.forPlayerInventory(player.inventory);
		Participant<Fluid> bucket = FabricParticipants.FLUID_ITEM.get().get(null, ItemKey.of(inHand), inHand.getCount(), participant.getHandReplacingParticipant(hand));
		if(player.isSneaking()) {
			int inserted = bucket.insert(Transaction.GLOBAL, Fluids.WATER, Droplet.BUCKET);
			player.sendMessage(new LiteralText("Inserted " + inserted + "dp"), false);
		} else {
			FixedObjectVolume<Fluid> fixed = new FixedObjectVolume<>(Fluids.EMPTY, Droplet.BUCKET);
			bucket.extract(Transaction.GLOBAL, fixed);
			player.sendMessage(new LiteralText("Extracted " + fixed.getQuantity(Transaction.GLOBAL) + "dp of " + Registry.FLUID.getId(fixed.getKey(Transaction.GLOBAL))), false);
		}
		return ActionResult.CONSUME;
	}

	public static class Tile extends BlockEntity implements io.github.astrarre.access.v0.fabric.provider.BlockEntityProvider {
		public FixedObjectVolume<Fluid> volume = new FixedObjectVolume<>(Fluids.EMPTY, Droplet.BUCKET);
		public Tile() {
			super(TestModMain.TANK_TYPE);
		}

		@Override
		public CompoundTag toTag(CompoundTag tag) {
			FixedObjectVolume.fixedSerializer(Fluids.EMPTY, FabricSerializers.FLUID).save((NBTagView.Builder) tag, "tank", this.volume);
			return super.toTag(tag);
		}

		@Override
		public void fromTag(BlockState state, CompoundTag tag) {
			this.volume = FixedObjectVolume.fixedSerializer(Fluids.EMPTY, FabricSerializers.FLUID).read(FabricViews.view(tag), "tank");
			super.fromTag(state, tag);
		}

		@Override
		public @Nullable Object get(Access<?> access, Direction direction) {
			if(access == FabricParticipants.FLUID_WORLD) {
				return this.volume;
			}
			return null;
		}
	}
}

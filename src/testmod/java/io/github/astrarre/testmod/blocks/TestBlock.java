package io.github.astrarre.testmod.blocks;

import io.github.astrarre.internal.gui.GuiScreenHandler;
import io.github.astrarre.v0.block.BaseBlock;
import io.github.astrarre.v0.block.BlockState;
import io.github.astrarre.v0.block.MinecraftBlocks;
import io.github.astrarre.v0.entity.Entity;
import io.github.astrarre.v0.entity.player.PlayerEntity;
import io.github.astrarre.v0.util.ActionResult;
import io.github.astrarre.v0.util.Hand;
import io.github.astrarre.v0.util.hit.BlockHitResult;
import io.github.astrarre.v0.util.math.BlockPos;
import io.github.astrarre.v0.world.World;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;

public class TestBlock extends BaseBlock implements ExtendedScreenHandlerFactory {
	public TestBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void onSteppedOn(World arg0, BlockPos arg1, Entity arg2) {
		arg0.setBlockState(arg1, MinecraftBlocks.AIR.getDefaultState());
	}

	public NamedScreenHandlerFactory createScreenHandlerFactory(net.minecraft.block.BlockState state, net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos) {
		return this;
	}

	@Override
	public ActionResult onUse(BlockState arg0, World arg1, BlockPos arg2, PlayerEntity arg3, Hand arg4, BlockHitResult arg5) {
		if (!arg1.isClient()) {
			//This will call the createScreenHandlerFactory method from BlockWithEntity, which will return our blockEntity casted to
			//a namedScreenHandlerFactory. If your block class does not extend BlockWithEntity, it needs to implement createScreenHandlerFactory.
			NamedScreenHandlerFactory screenHandlerFactory = ((net.minecraft.block.BlockState)arg0).createScreenHandlerFactory((net.minecraft.world.World) arg1, (net.minecraft.util.math.BlockPos) arg2);

			if (screenHandlerFactory != null) {
				//With this call the server will request the client to open the appropriate Screenhandler
				((net.minecraft.entity.player.PlayerEntity)arg3).openHandledScreen(screenHandlerFactory);
			}
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText("ohno.ohno");
	}

	@Override
	public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, net.minecraft.entity.player.PlayerEntity player) {
		return new GuiScreenHandler(syncId, inv);
	}

	@Override
	public void writeScreenOpeningData(ServerPlayerEntity entity, PacketByteBuf buf) {

	}
}

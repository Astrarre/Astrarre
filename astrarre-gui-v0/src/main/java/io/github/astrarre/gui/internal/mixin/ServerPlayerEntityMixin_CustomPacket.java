package io.github.astrarre.gui.internal.mixin;

import java.util.OptionalInt;

import com.mojang.authlib.GameProfile;
import io.github.astrarre.gui.internal.AstrarreInitializer;
import io.github.astrarre.gui.internal.access.ScreenRootAccess;
import io.github.astrarre.gui.internal.vanilla.DefaultScreenHandler;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.astrarre.networking.v0.api.ModPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@Mixin (ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin_CustomPacket extends PlayerEntity {
	@Unique
	private final ThreadLocal<ScreenHandler> astrarre_currentHandler = new ThreadLocal<>();

	public ServerPlayerEntityMixin_CustomPacket(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject (method = "openHandledScreen(Lnet/minecraft/screen/NamedScreenHandlerFactory;)Ljava/util/OptionalInt;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void astrarre_storeOpenedScreenHandler(NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> info, ScreenHandler handler) {
		this.astrarre_currentHandler.set(handler);
	}

	@ModifyArg (method = "openHandledScreen",
			at = @At (value = "INVOKE",
					target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
	public Packet<?> onSendPacket(Packet<?> current) {
		ScreenHandler handler = this.astrarre_currentHandler.get();
		if (handler instanceof DefaultScreenHandler) {
			OpenScreenS2CPacket s2c = (OpenScreenS2CPacket) current;
			NBTagView.Builder builder = NBTagView.builder()
			                                     .putInt("syncId", handler.syncId)
			                                     .putInt("screenHandlerTypeId", Registry.SCREEN_HANDLER.getRawId(handler.getType()));
			FabricSerializers.TEXT.save(builder, "name", s2c.getName());
			((ScreenRootAccess)handler).getRoot().write(builder);
			ModPacketHandler.INSTANCE.sendToClient((ServerPlayerEntity) (Object) this, AstrarreInitializer.CHANNEL, builder);
			return AstrarreInitializer.FAKE;
		}
		return current;
	}
}

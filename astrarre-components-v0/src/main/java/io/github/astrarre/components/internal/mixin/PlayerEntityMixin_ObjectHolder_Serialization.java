package io.github.astrarre.components.internal.mixin;

import java.util.Map;

import com.mojang.datafixers.util.Pair;
import io.github.astrarre.components.internal.ComponentsInternal;
import io.github.astrarre.components.internal.access.PlayerDataObjectHolder;
import io.github.astrarre.components.internal.lazyAsm.standard.CopyAccess;
import io.github.astrarre.components.v0.api.components.Component;
import io.github.astrarre.components.v0.fabric.FabricComponents;
import io.github.astrarre.components.v0.fabric.FabricSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin_ObjectHolder_Serialization implements PlayerDataObjectHolder {
	public CopyAccess astrarre_access_player;
	public int astrarre_version_player;

	@Override
	public CopyAccess astrarre_getObject_p() {
		return this.astrarre_access_player;
	}

	@Override
	public int astrarre_getVersion_p() {
		return this.astrarre_version_player;
	}

	@Override
	public void astrarre_setObject_p(CopyAccess object, int version) {
		this.astrarre_access_player = object;
		this.astrarre_version_player = version;
	}

	@Inject (method = "writeCustomDataToNbt", at = @At ("RETURN"))
	public void writeNbt(NbtCompound nbt, CallbackInfo ci) {
		NbtCompound componentData = new NbtCompound();
		for (Map.Entry<String, Pair<Component<PlayerEntity, ?>, FabricSerializer<?, ?>>> entry : ComponentsInternal.SERIALIZE_PLAYER_INTERNAL.entrySet()) {
			Pair<Component<PlayerEntity, ?>, FabricSerializer<?, ?>> value = entry.getValue();
			componentData.put(entry.getKey(), FabricComponents.serialize((Entity) (Object) this, (Component)value.getFirst(), value.getSecond()));
		}
		nbt.put("astrarre_components", componentData);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
	public void readNbt(NbtCompound nbt, CallbackInfo ci) {
		NbtCompound componentData = nbt.getCompound("astrarre_components");
		for (String key : componentData.getKeys()) {
			Pair<Component<PlayerEntity, ?>, FabricSerializer<?, ?>> pair = ComponentsInternal.SERIALIZE_PLAYER_INTERNAL.get(key);
			if(pair != null) {
				FabricComponents.deserialize(componentData.get(key), (Entity) (Object) this, (Component) pair.getFirst(), (FabricSerializer) pair.getSecond());
			}
			// name changed, perhaps some DFU stuff later(?)
		}
	}
}

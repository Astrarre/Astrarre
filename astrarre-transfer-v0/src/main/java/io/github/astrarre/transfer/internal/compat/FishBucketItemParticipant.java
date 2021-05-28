package io.github.astrarre.transfer.internal.compat;

import io.github.astrarre.itemview.v0.fabric.ItemKey;
import io.github.astrarre.transfer.internal.mixin.FishBucketItemAccess_EntityTypeAccess;
import io.github.astrarre.transfer.v0.api.ReplacingParticipant;
import io.github.astrarre.transfer.v0.api.transaction.Transaction;
import io.github.astrarre.util.v0.fabric.MinecraftServers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class FishBucketItemParticipant extends BucketItemParticipant {
	public FishBucketItemParticipant(ItemKey key, int quantity, ReplacingParticipant<ItemKey> container) {
		super(key, quantity, container);
	}

	@Override
	protected boolean extractTest(Transaction transaction, ItemKey current, int quantity) {
		Item bucket = current.getItem();
		if(bucket instanceof FishBucketItemAccess_EntityTypeAccess) {
			if(super.extractTest(transaction, current, quantity)) {
				EntityType<?> type = ((FishBucketItemAccess_EntityTypeAccess) bucket).getEntityType();
				MinecraftServer server = MinecraftServers.activeServer;
				if(server != null) {
					LootManager manager = server.getLootManager();
					LootTable table = manager.getTable(type.getLootTableId());
					ServerWorld overworld = server.getOverworld(); // todo perhaps an access that gives more context like position and world?
					Entity fish = type.create(overworld);
					if(fish == null) {
						return false;
					}
					fish.setPos(this.origin().x, this.origin().y, this.origin().z);
					LootContext context = new LootContext.Builder(overworld)
							.random(overworld.random)
							.parameter(LootContextParameters.THIS_ENTITY, fish)
							.parameter(LootContextParameters.DAMAGE_SOURCE, DamageSource.OUT_OF_WORLD)
							.parameter(LootContextParameters.ORIGIN, this.origin())
							.build(LootContextTypes.ENTITY);
					for (ItemStack stack : table.generateLoot(context)) {
						if(this.container.insert(transaction, ItemKey.of(stack), stack.getCount()) != stack.getCount()) {
							fish.remove(Entity.RemovalReason.DISCARDED);
							return false;
						}
					}
					fish.remove(Entity.RemovalReason.DISCARDED);
					return true;
				}
			}
			return false;
		}
		return super.extractTest(transaction, current, quantity);
	}

	private static final Vec3d OUT_OF_BOUNDS = new Vec3d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
	protected Vec3d origin() {
		return OUT_OF_BOUNDS;
	}
}

package snownee.pdgamerules.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import snownee.pdgamerules.duck.PDDerivedLevelData;
import snownee.pdgamerules.duck.PDPrimaryLevelData;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@ModifyExpressionValue(
			method = "createLevels",
			at = @At(
					value = "NEW",
					target = "net/minecraft/world/level/storage/DerivedLevelData"
			)
	)
	private DerivedLevelData pdgamerules_createLevels(
			final DerivedLevelData derivedLevelData,
			@Local final ServerLevelData serverLevelData,
			@Local(ordinal = 1) final ResourceKey resourceKey2) {
		if (serverLevelData instanceof PDPrimaryLevelData primaryLevelData) {
			((PDDerivedLevelData) derivedLevelData)
					.pdgamerules$putData(primaryLevelData.pdgamerules$getOrCreateData(resourceKey2));
		}
		return derivedLevelData;
	}
}

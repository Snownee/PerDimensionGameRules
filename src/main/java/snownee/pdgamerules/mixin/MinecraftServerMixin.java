package snownee.pdgamerules.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import snownee.pdgamerules.duck.PDDerivedLevelData;
import snownee.pdgamerules.duck.PDPrimaryLevelData;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Inject(
			method = "createLevels", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/storage/DerivedLevelData;<init>(Lnet/minecraft/world/level/storage/WorldData;Lnet/minecraft/world/level/storage/ServerLevelData;)V",
			shift = At.Shift.BY,
			by = 2
	), locals = LocalCapture.CAPTURE_FAILHARD)
	private void pdgamerules_createLevels(
			ChunkProgressListener p_129816_,
			CallbackInfo ci,
			ServerLevelData serverleveldata,
			boolean flag,
			Registry<LevelStem> registry,
			WorldOptions worldoptions,
			long i,
			long j,
			List<CustomSpawner> list,
			LevelStem levelstem,
			ServerLevel serverlevel,
			DimensionDataStorage dimensiondatastorage,
			WorldBorder worldborder,
			RandomSequences randomsequences,
			Iterator<Map.Entry<ResourceKey<LevelStem>, LevelStem>> var16,
			Map.Entry<ResourceKey<LevelStem>, LevelStem> entry,
			ResourceKey<LevelStem> resourcekey,
			ResourceKey<Level> resourcekey1,
			DerivedLevelData derivedleveldata) {
		if (serverleveldata instanceof PDPrimaryLevelData primaryLevelData) {
			((PDDerivedLevelData) derivedleveldata).pdgamerules$putData(primaryLevelData.pdgamerules$getOrCreateData(resourcekey1));
		}
	}
}

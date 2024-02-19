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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import snownee.pdgamerules.duck.PDDerivedLevelData;
import snownee.pdgamerules.duck.PDPrimaryLevelData;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Inject(
			method = "createLevels",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/storage/DerivedLevelData;<init>" +
							 "(Lnet/minecraft/world/level/storage/WorldData;" +
							 "Lnet/minecraft/world/level/storage/ServerLevelData;)V",
					shift = At.Shift.BY,
					by = 2
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void pdgamerules_createLevels(
			final ChunkProgressListener chunkProgressListener,
			final CallbackInfo ci,
			final ServerLevelData serverLevelData,
			final boolean bl,
			final Registry registry,
			final WorldOptions worldOptions,
			final long l,
			final long m,
			final List list,
			final LevelStem levelStem,
			final ServerLevel serverLevel,
			final WorldBorder worldBorder,
			final RandomSequences randomSequences,
			final Iterator var16,
			final Map.Entry entry,
			final ResourceKey<LevelStem> resourceKey,
			final ResourceKey<Level> resourceKey2,
			final DerivedLevelData derivedLevelData
	) {
		if (serverLevelData instanceof PDPrimaryLevelData primaryLevelData) {
			((PDDerivedLevelData) derivedLevelData)
					.pdgamerules$putData(primaryLevelData.pdgamerules$getOrCreateData(resourceKey2));
		}
	}
}

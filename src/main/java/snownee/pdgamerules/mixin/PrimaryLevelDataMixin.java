package snownee.pdgamerules.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.PrimaryLevelData;
import snownee.pdgamerules.PDDerivedGameRuleData;
import snownee.pdgamerules.PDGameRulesMod;
import snownee.pdgamerules.duck.PDPrimaryLevelData;

@Mixin(PrimaryLevelData.class)
public class PrimaryLevelDataMixin implements PDPrimaryLevelData {
	@Unique
	private final Map<ResourceKey<Level>, PDDerivedGameRuleData> pdgamerules$dataMap = Maps.newHashMap();

	@Inject(method = "parse", at = @At("RETURN"))
	private static <T> void pdgamerules_parse(Dynamic<T> dynamic, DataFixer p_78532_, int p_78533_, CompoundTag p_78534_, LevelSettings p_78535_, LevelVersion p_78536_, PrimaryLevelData.SpecialWorldProperty p_250651_, WorldOptions p_251864_, Lifecycle p_78538_, CallbackInfoReturnable<PrimaryLevelData> cir) {
		if (cir.getReturnValue() instanceof PDPrimaryLevelData data) {
			Dynamic<T> rulesDynamic = dynamic.get("PDGameRules").orElseEmptyMap();
			data.pdgamerules$putData(PDDerivedGameRuleData.DATA_MAP_CODEC.decode(rulesDynamic).result().orElseThrow().getFirst());
		}
	}

	@Inject(method = "setTagData", at = @At("RETURN"))
	private void pdgamerules_setTagData(RegistryAccess registryAccess, CompoundTag tag, CompoundTag p_78548_, CallbackInfo ci) {
		pdgamerules$dataMap.values().removeIf($ -> !$.independentDayTime && !$.independentWeather);
		if (!pdgamerules$dataMap.isEmpty()) {
			PDGameRulesMod.LOGGER.debug(pdgamerules$dataMap.toString());
			PDDerivedGameRuleData.DATA_MAP_CODEC.encodeStart(NbtOps.INSTANCE, pdgamerules$dataMap).result()
					.ifPresent($ -> tag.put("PDGameRules", $));
		}
	}

	@Override
	public void pdgamerules$putData(Map<ResourceKey<Level>, PDDerivedGameRuleData> dataMap) {
		pdgamerules$dataMap.putAll(dataMap);
	}

	@Override
	public PDDerivedGameRuleData pdgamerules$getOrCreateData(ResourceKey<Level> key) {
		return pdgamerules$dataMap.computeIfAbsent(key, $ -> new PDDerivedGameRuleData());
	}
}

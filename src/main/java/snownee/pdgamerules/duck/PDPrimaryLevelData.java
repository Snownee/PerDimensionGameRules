package snownee.pdgamerules.duck;

import java.util.Map;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import snownee.pdgamerules.PDDerivedGameRuleData;

public interface PDPrimaryLevelData {
	void pdgamerules$putData(Map<ResourceKey<Level>, PDDerivedGameRuleData> dataMap);

	PDDerivedGameRuleData pdgamerules$getOrCreateData(ResourceKey<Level> key);
}

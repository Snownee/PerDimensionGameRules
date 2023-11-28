package snownee.pdgamerules;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = PDGameRulesMod.ID)
public class PDGameRulesConfig implements ConfigData {
	public Map<String, Map<String, Object>> rules = ImmutableMap.of("minecraft:the_nether", ImmutableMap.of("doMobLoot", "false"));
}

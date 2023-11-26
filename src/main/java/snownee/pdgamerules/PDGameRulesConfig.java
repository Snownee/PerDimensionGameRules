package snownee.pdgamerules;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import snownee.kiwi.config.KiwiConfig;

@KiwiConfig
public class PDGameRulesConfig {

	public static Map<String, Map<String, Object>> rules = ImmutableMap.of("minecraft:the_nether", ImmutableMap.of("doMobLoot", "false"));

}

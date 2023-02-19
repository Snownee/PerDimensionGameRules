package snownee.pdgamerules;

import java.util.Map;

import snownee.kiwi.config.KiwiConfig;

@KiwiConfig
public class PDGameRulesConfig {

	public static Map<String, Map<String, Object>> rules = Map.of("minecraft:the_nether", Map.of("doMobLoot", "false"));

}

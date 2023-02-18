package snownee.perworldgamerules;

import java.util.Map;

import snownee.kiwi.config.KiwiConfig;

@KiwiConfig
public class PWGameRulesConfig {

	public static Map<String, Map<String, Object>> rules = Map.of("minecraft:the_nether", Map.of("doMobLoot", "false"));

}

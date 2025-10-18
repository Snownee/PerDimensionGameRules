package snownee.pdgamerules;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.Util;
import snownee.kiwi.config.KiwiConfig;

@KiwiConfig
public class PDGameRulesConfig {

	public static Map<String, Map<String, Object>> rules = Util.make(
			Maps.newHashMap(),
			(rules) -> rules.put(
					"minecraft:the_nether",
					Util.make(Maps.newHashMap(), (it) -> it.put("doMobLoot", "false"))
			));
}

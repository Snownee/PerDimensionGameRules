package snownee.pdgamerules;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.Util;
import snownee.kiwi.config.KiwiConfig;

@KiwiConfig
public class PDGameRulesConfig {
	public static final String DIMENSION_TYPE_PREFIX = "@";

	public static Map<String, Map<String, Object>> rules = Util.make(
			Maps.newHashMap(),
			(rules) -> {
				rules.put(
						"minecraft:the_nether",
						Util.make(Maps.newHashMap(), (it) -> it.put("doMobLoot", "false"))
				);
				rules.put(
						DIMENSION_TYPE_PREFIX + "minecraft:the_nether",
						Util.make(Maps.newHashMap(), (it) -> it.put("keepInventory", "true"))
				);
			});

	@KiwiConfig.Listen("rules")
	public static void onRulesChanged(String path) {
		PDGameRules.onConfigReload();
	}
}

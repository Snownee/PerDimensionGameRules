package snownee.pdgamerules;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.util.Util;
import snownee.kiwi.config.KiwiConfig;

@KiwiConfig
public class PDGameRulesConfig {
	public static final String DIMENSION_TYPE_PREFIX = "@";

	/**
	 * Game rules were renamed in Minecraft 26.1. Rules set by older versions of this mod
	 * are migrated to their new names so existing configs keep working.
	 */
	private static final Map<String, String> LEGACY_RULE_NAMES = Map.ofEntries(
			Map.entry("allowEnteringNetherUsingPortals", "allow_entering_nether_using_portals"),
			Map.entry("announceAdvancements", "show_advancement_messages"),
			Map.entry("blockExplosionDropDecay", "block_explosion_drop_decay"),
			Map.entry("commandBlockOutput", "command_block_output"),
			Map.entry("enableCommandBlocks", "command_blocks_work"),
			Map.entry("commandBlocksEnabled", "command_blocks_work"),
			Map.entry("commandModificationBlockLimit", "max_block_modifications"),
			Map.entry("disableElytraMovementCheck", "elytra_movement_check"),
			Map.entry("disablePlayerMovementCheck", "player_movement_check"),
			Map.entry("disableRaids", "raids"),
			Map.entry("doDaylightCycle", "advance_time"),
			Map.entry("doEntityDrops", "entity_drops"),
			Map.entry("doImmediateRespawn", "immediate_respawn"),
			Map.entry("doInsomnia", "spawn_phantoms"),
			Map.entry("doLimitedCrafting", "limited_crafting"),
			Map.entry("doMobLoot", "mob_drops"),
			Map.entry("doMobSpawning", "spawn_mobs"),
			Map.entry("doPatrolSpawning", "spawn_patrols"),
			Map.entry("doTileDrops", "block_drops"),
			Map.entry("doTraderSpawning", "spawn_wandering_traders"),
			Map.entry("doVinesSpread", "spread_vines"),
			Map.entry("doWardenSpawning", "spawn_wardens"),
			Map.entry("doWeatherCycle", "advance_weather"),
			Map.entry("drowningDamage", "drowning_damage"),
			Map.entry("enderPearlsVanishOnDeath", "ender_pearls_vanish_on_death"),
			Map.entry("fallDamage", "fall_damage"),
			Map.entry("fireDamage", "fire_damage"),
			Map.entry("forgiveDeadPlayers", "forgive_dead_players"),
			Map.entry("freezeDamage", "freeze_damage"),
			Map.entry("globalSoundEvents", "global_sound_events"),
			Map.entry("keepInventory", "keep_inventory"),
			Map.entry("lavaSourceConversion", "lava_source_conversion"),
			Map.entry("locatorBar", "locator_bar"),
			Map.entry("logAdminCommands", "log_admin_commands"),
			Map.entry("maxCommandChainLength", "max_command_sequence_length"),
			Map.entry("maxCommandForkCount", "max_command_forks"),
			Map.entry("maxEntityCramming", "max_entity_cramming"),
			Map.entry("minecartMaxSpeed", "max_minecart_speed"),
			Map.entry("mobExplosionDropDecay", "mob_explosion_drop_decay"),
			Map.entry("mobGriefing", "mob_griefing"),
			Map.entry("naturalRegeneration", "natural_health_regeneration"),
			Map.entry("playersNetherPortalCreativeDelay", "players_nether_portal_creative_delay"),
			Map.entry("playersNetherPortalDefaultDelay", "players_nether_portal_default_delay"),
			Map.entry("playersSleepingPercentage", "players_sleeping_percentage"),
			Map.entry("projectilesCanBreakBlocks", "projectiles_can_break_blocks"),
			Map.entry("pvp", "pvp"),
			Map.entry("randomTickSpeed", "random_tick_speed"),
			Map.entry("reducedDebugInfo", "reduced_debug_info"),
			Map.entry("sendCommandFeedback", "send_command_feedback"),
			Map.entry("showDeathMessages", "show_death_messages"),
			Map.entry("snowAccumulationHeight", "max_snow_accumulation_height"),
			Map.entry("spawnMonsters", "spawn_monsters"),
			Map.entry("spawnRadius", "respawn_radius"),
			Map.entry("spawnerBlocksEnabled", "spawner_blocks_work"),
			Map.entry("spectatorsGenerateChunks", "spectators_generate_chunks"),
			Map.entry("tntExplodes", "tnt_explodes"),
			Map.entry("tntExplosionDropDecay", "tnt_explosion_drop_decay"),
			Map.entry("universalAnger", "universal_anger"),
			Map.entry("waterSourceConversion", "water_source_conversion")
	);
	private static final Set<String> INVERTED_LEGACY_RULE_NAMES = Sets.newHashSet(
			"disableElytraMovementCheck",
			"disablePlayerMovementCheck",
			"disableRaids"
	);

	public static Map<String, Map<String, Object>> rules = Util.make(
			Maps.newHashMap(),
			(rules) -> {
				rules.put(
						"minecraft:the_nether",
						Util.make(Maps.newHashMap(), (it) -> it.put("mob_drops", "false"))
				);
				rules.put(
						DIMENSION_TYPE_PREFIX + "minecraft:the_nether",
						Util.make(Maps.newHashMap(), (it) -> it.put("keep_inventory", "true"))
				);
			});

	public static void migrateLegacyRuleNames() {
		for (Map<String, Object> layerRules : rules.values()) {
			for (Map.Entry<String, Object> entry : new ArrayList<>(layerRules.entrySet())) {
				String newId = LEGACY_RULE_NAMES.get(entry.getKey());
				if (newId == null) {
					continue;
				}
				layerRules.remove(entry.getKey());
				if (layerRules.containsKey(newId)) {
					continue;
				}
				Object value = entry.getValue();
				if (INVERTED_LEGACY_RULE_NAMES.contains(entry.getKey())) {
					value = String.valueOf(!Boolean.parseBoolean(String.valueOf(value)));
				}
				layerRules.put(newId, value);
			}
		}
	}

	@KiwiConfig.Listen("rules")
	public static void onRulesChanged(String path) {
		migrateLegacyRuleNames();
		PDGameRules.onConfigReload();
	}
}

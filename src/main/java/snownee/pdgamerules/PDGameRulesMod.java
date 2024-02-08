package snownee.pdgamerules;

import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import snownee.pdgamerules.duck.PDDerivedLevelData;

@Mod("pdgamerules")
public class PDGameRulesMod {

	public static final Logger LOGGER = LogUtils.getLogger();
	public static final Set<GameRules.Key<?>> UNSUPPORTED_GAME_RULES = Sets.newHashSet();
	public static final Set<GameRules.Key<?>> UNSUPPORTED_GAME_RULES_OVERWORLD = Sets.newHashSet();
	private static int generation = 0;

	public PDGameRulesMod() {
		UNSUPPORTED_GAME_RULES.add(GameRules.RULE_SENDCOMMANDFEEDBACK);
		UNSUPPORTED_GAME_RULES.add(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
		UNSUPPORTED_GAME_RULES_OVERWORLD.add(GameRules.RULE_DAYLIGHT);
		UNSUPPORTED_GAME_RULES_OVERWORLD.add(GameRules.RULE_WEATHER_CYCLE);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
	}

	private void registerCommands(RegisterCommandsEvent event) {
		PDGameRuleCommand.register(event.getDispatcher());
	}

	public static boolean isSupported(GameRules.Key<?> key) {
		return !UNSUPPORTED_GAME_RULES.contains(key);
	}

	public static boolean canUseInOverworld(GameRules.Key<?> key) {
		return !UNSUPPORTED_GAME_RULES_OVERWORLD.contains(key);
	}

	public static int getGeneration() {
		return generation;
	}

	public static void iterateGeneration() {
		generation++;
	}

	public static void onPDRuleChange(ServerLevel level, GameRules.Key<?> key, @Nullable String value) {
		if (key == GameRules.RULE_DAYLIGHT || key == GameRules.RULE_WEATHER_CYCLE) {
			if (level.getLevelData() instanceof PDDerivedLevelData data) {
				if (key == GameRules.RULE_DAYLIGHT) {
					data.pdgamerules$setIndependentDayTime(value != null);
				} else {
					data.pdgamerules$setIndependentWeather(value != null);
				}
			}
		}
		iterateGeneration();
	}

}

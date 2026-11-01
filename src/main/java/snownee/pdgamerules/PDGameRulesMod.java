package snownee.pdgamerules;

import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.saveddata.WeatherData;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.level.GameRuleChangedEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Mod("pdgamerules")
public class PDGameRulesMod {

	public static final Logger LOGGER = LogUtils.getLogger();
	public static final Set<GameRule<?>> UNSUPPORTED_GAME_RULES = Sets.newHashSet();
	public static final Set<GameRule<?>> UNSUPPORTED_GAME_RULES_OVERWORLD = Sets.newHashSet();
	private static final Set<ResourceKey<Level>> INDEPENDENT_WEATHER_DIMENSIONS = Sets.newHashSet();
	private static int generation = 0;
	@Nullable
	private static MinecraftServer server;

	public PDGameRulesMod() {
		UNSUPPORTED_GAME_RULES.add(GameRules.SEND_COMMAND_FEEDBACK);
		UNSUPPORTED_GAME_RULES.add(GameRules.MAX_COMMAND_SEQUENCE_LENGTH);
		UNSUPPORTED_GAME_RULES.add(GameRules.REDUCED_DEBUG_INFO);
		UNSUPPORTED_GAME_RULES_OVERWORLD.add(GameRules.ADVANCE_TIME);
		UNSUPPORTED_GAME_RULES_OVERWORLD.add(GameRules.ADVANCE_WEATHER);
		NeoForge.EVENT_BUS.addListener(this::registerCommands);
		NeoForge.EVENT_BUS.addListener(this::onGameRuleChanged);
		NeoForge.EVENT_BUS.addListener(this::onServerStarted);
		NeoForge.EVENT_BUS.addListener(this::onServerStopped);
	}

	private void registerCommands(RegisterCommandsEvent event) {
		PDGameRuleCommand.register(event.getDispatcher(), event.getBuildContext());
	}

	private void onGameRuleChanged(GameRuleChangedEvent event) {
		onGlobalRuleChange(event.getGameRule());
	}

	private void onServerStarted(ServerStartedEvent event) {
		server = event.getServer();
		applyClockStates();
		refreshIndependentWeather(false);
	}

	private void onServerStopped(ServerStoppedEvent event) {
		server = null;
		INDEPENDENT_WEATHER_DIMENSIONS.clear();
	}

	public static boolean isSupported(GameRule<?> rule) {
		return !UNSUPPORTED_GAME_RULES.contains(rule);
	}

	public static boolean canUseInOverworld(GameRule<?> rule) {
		return !UNSUPPORTED_GAME_RULES_OVERWORLD.contains(rule);
	}

	public static int getGeneration() {
		return generation;
	}

	public static void iterateGeneration() {
		generation++;
	}

	public static void onPDRuleChange(ServerLevel level, GameRule<?> rule, @Nullable String value) {
		iterateGeneration();
		if (rule == GameRules.ADVANCE_TIME) {
			applyClockStates();
		}
		if (rule == GameRules.ADVANCE_WEATHER) {
			refreshIndependentWeather(true);
		}
	}

	public static void onGlobalRuleChange(GameRule<?> rule) {
		iterateGeneration();
		if (rule == GameRules.ADVANCE_TIME) {
			applyClockStates();
		}
	}

	/**
	 * Vanilla ticks all world clocks based on the global game rules. Pause the clock of a dimension
	 * when its own {@code advance_time} rule is disabled, so per-dimension time still works.
	 */
	public static void applyClockStates() {
		MinecraftServer server = PDGameRulesMod.server;
		if (server == null) {
			return;
		}
		if (Thread.currentThread() != server.getRunningThread()) {
			server.executeIfPossible(PDGameRulesMod::applyClockStates);
			return;
		}
		for (ServerLevel level : server.getAllLevels()) {
			level.dimensionType().defaultClock().ifPresent(clock -> {
				boolean advanceTime = level.getGameRules().get(GameRules.ADVANCE_TIME);
				server.clockManager().setPaused(clock, !advanceTime);
			});
		}
	}

	/**
	 * A dimension gets its own weather data as soon as {@code advance_weather} is configured for it.
	 * The data is stored in that dimension's own saved data directory, just like vanilla stores it
	 * for the overworld.
	 */
	public static boolean hasIndependentWeather(ServerLevel level) {
		return level.getGameRules() instanceof PDGameRules rules && rules.hasExplicitRule(GameRules.ADVANCE_WEATHER);
	}

	public static WeatherData getIndependentWeather(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(WeatherData.TYPE);
	}

	public static void refreshIndependentWeather(boolean copyNewDimensions) {
		MinecraftServer server = PDGameRulesMod.server;
		if (server == null) {
			return;
		}
		if (Thread.currentThread() != server.getRunningThread()) {
			server.executeIfPossible(() -> refreshIndependentWeather(copyNewDimensions));
			return;
		}
		Set<ResourceKey<Level>> current = Sets.newHashSet();
		for (ServerLevel level : server.getAllLevels()) {
			if (!hasIndependentWeather(level)) {
				continue;
			}
			ResourceKey<Level> dimension = level.dimension();
			current.add(dimension);
			if (copyNewDimensions && !INDEPENDENT_WEATHER_DIMENSIONS.contains(dimension)) {
				copyWeather(server.getWeatherData(), getIndependentWeather(level));
			}
		}
		INDEPENDENT_WEATHER_DIMENSIONS.clear();
		INDEPENDENT_WEATHER_DIMENSIONS.addAll(current);
	}

	private static void copyWeather(WeatherData from, WeatherData to) {
		to.setClearWeatherTime(from.getClearWeatherTime());
		to.setRainTime(from.getRainTime());
		to.setThunderTime(from.getThunderTime());
		to.setRaining(from.isRaining());
		to.setThundering(from.isThundering());
	}

}

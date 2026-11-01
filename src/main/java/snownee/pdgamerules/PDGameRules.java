package snownee.pdgamerules;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.minecraft.world.level.gamerules.GameRules;

public class PDGameRules extends GameRules {
	private static final Set<String> VALID_RULES = Sets.newHashSet();
	private static final Set<String> WARNED_RULES = Sets.newHashSet();
	private static boolean validated;

	private final GameRules parent;
	private final String dimension;
	@Nullable
	private final String dimensionType;
	private final boolean overworld;
	private final Cache<GameRule<?>, Object> cache = CacheBuilder.newBuilder().build();

	public PDGameRules(GameRules parent, ResourceKey<Level> dimension, @Nullable String dimensionType) {
		super(List.of());
		this.parent = parent;
		this.dimension = dimension.identifier().toString();
		this.dimensionType = dimensionType;
		this.overworld = Level.OVERWORLD.equals(dimension);
		validateRules();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(GameRule<T> rule) {
		try {
			return (T) cache.get(rule, () -> resolveRule(rule));
		} catch (Exception ignored) {
		}
		return parent.get(rule);
	}

	private <T> Object resolveRule(GameRule<T> rule) {
		T value = parseRule(dimension, rule);
		if (value == null && dimensionType != null) {
			value = parseRule(PDGameRulesConfig.DIMENSION_TYPE_PREFIX + dimensionType, rule);
		}
		return value == null ? parent.get(rule) : value;
	}

	public boolean hasExplicitRule(GameRule<?> rule) {
		if (overworld && !PDGameRulesMod.canUseInOverworld(rule)) {
			return false;
		}
		if (PDGameRulesConfig.rules.getOrDefault(dimension, Map.of()).containsKey(rule.id())) {
			return true;
		}
		return dimensionType != null && PDGameRulesConfig.rules
				.getOrDefault(PDGameRulesConfig.DIMENSION_TYPE_PREFIX + dimensionType, Map.of())
				.containsKey(rule.id());
	}

	private <T> @Nullable T parseRule(String layer, GameRule<T> rule) {
		Object rawValue = PDGameRulesConfig.rules.getOrDefault(layer, Map.of()).get(rule.id());
		if (rawValue == null || overworld && !PDGameRulesMod.canUseInOverworld(rule)) {
			return null;
		}
		try {
			return rule.deserialize(String.valueOf(rawValue)).result().orElse(null);
		} catch (Exception e) {
			warnOnce(rule.id(), "Failed to parse game rule '{}' in config layer '{}': {}", rule.id(), layer, rawValue);
			return null;
		}
	}

	private static void validateRules() {
		if (validated) {
			return;
		}
		validated = true;
		PDGameRulesConfig.migrateLegacyRuleNames();
		VALID_RULES.clear();
		BuiltInRegistries.GAME_RULE.listElements().forEach($ -> VALID_RULES.add($.value().id()));
		PDGameRulesConfig.rules.forEach((layer, layerRules) -> {
			for (String rule : layerRules.keySet()) {
				if (!VALID_RULES.contains(rule)) {
					warnOnce(rule, "Unknown game rule '{}' configured in layer '{}'", rule, layer);
				}
			}
		});
	}

	private static void warnOnce(String rule, String message, Object... args) {
		if (WARNED_RULES.add(rule)) {
			PDGameRulesMod.LOGGER.warn(message, args);
		}
	}

	public static void onConfigReload() {
		validated = false;
		PDGameRulesMod.iterateGeneration();
		PDGameRulesMod.applyClockStates();
		PDGameRulesMod.refreshIndependentWeather(true);
	}

	public void invalidate() {
		cache.invalidateAll();
	}

	@Override
	public <T> void set(GameRule<T> rule, T value, @Nullable MinecraftServer server) {
		parent.set(rule, value, server);
	}

	@Override
	public <T> String getAsString(GameRule<T> rule) {
		return rule.serialize(get(rule));
	}

	@Override
	public GameRules copy(FeatureFlagSet enabledFeatures) {
		return parent.copy(enabledFeatures);
	}

	@Override
	public void setAll(GameRules other, @Nullable MinecraftServer server) {
		parent.setAll(other, server);
	}

	@Override
	public void setAll(GameRuleMap gameRulesMap, @Nullable MinecraftServer server) {
		parent.setAll(gameRulesMap, server);
	}

	@Override
	public Stream<GameRule<?>> availableRules() {
		return parent.availableRules();
	}

	@Override
	public void visitGameRuleTypes(GameRuleTypeVisitor visitor) {
		parent.visitGameRuleTypes(visitor);
	}
}

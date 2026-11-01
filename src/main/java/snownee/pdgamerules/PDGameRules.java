package snownee.pdgamerules;

import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import snownee.pdgamerules.mixin.GameRulesAccess;
import snownee.pdgamerules.mixin.GameRulesValueAccess;

public class PDGameRules extends GameRules {
	private static final Set<String> VALID_RULES = Sets.newHashSet();
	private static final Set<String> WARNED_RULES = Sets.newHashSet();
	private static boolean validated;

	private final GameRules parent;
	private final String dimension;
	@Nullable
	private final String dimensionType;
	private final boolean overworld;
	private final Cache<Key<?>, Value<?>> cache = CacheBuilder.newBuilder().build();

	public PDGameRules(GameRules parent, ResourceKey<Level> dimension, @Nullable String dimensionType) {
		this.parent = parent;
		this.dimension = dimension.location().toString();
		this.dimensionType = dimensionType;
		this.overworld = Level.OVERWORLD.equals(dimension);
		((GameRulesAccess) this).setRules(Map.of());
		validateRules();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Value<T>> @NotNull T getRule(Key<T> key) {
		try {
			return (T) cache.get(key, () -> resolveRule(key));
		} catch (Exception ignored) {
		}
		return parent.getRule(key);
	}

	private <T extends Value<T>> Value<T> resolveRule(Key<T> key) {
		Value<T> value = parseRule(dimension, key);
		if (value == null && dimensionType != null) {
			value = parseRule(PDGameRulesConfig.DIMENSION_TYPE_PREFIX + dimensionType, key);
		}
		return value == null ? parent.getRule(key) : value;
	}

	@SuppressWarnings("unchecked")
	private <T extends Value<T>> Value<T> parseRule(String layer, Key<T> key) {
		Object rawValue = PDGameRulesConfig.rules.getOrDefault(layer, Map.of()).get(key.getId());
		if (rawValue == null || overworld && !PDGameRulesMod.canUseInOverworld(key)) {
			return null;
		}
		try {
			Type<T> type = ((GameRulesValueAccess<T>) parent.getRule(key)).getType();
			Value<T> value = type.createRule();
			((GameRulesValueAccess<T>) value).callDeserialize(String.valueOf(rawValue));
			return value;
		} catch (Exception e) {
			warnOnce(key.getId(), "Failed to parse game rule '{}' in config layer '{}': {}", key.getId(), layer, rawValue);
			return null;
		}
	}

	private static void validateRules() {
		if (validated) {
			return;
		}
		validated = true;
		VALID_RULES.clear();
		GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
			@Override
			public <T extends Value<T>> void visit(Key<T> key, Type<T> type) {
				VALID_RULES.add(key.getId());
			}
		});
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
	}

	public void invalidate() {
		cache.invalidateAll();
	}

	@Override
	public void assignFrom(GameRules rules, @Nullable MinecraftServer server) {
		parent.assignFrom(rules, server);
	}

	@Override
	public @NotNull GameRules copy() {
		return parent.copy();
	}

	@Override
	public @NotNull CompoundTag createTag() {
		return parent.createTag();
	}
}

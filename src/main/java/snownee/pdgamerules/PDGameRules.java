package snownee.pdgamerules;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import snownee.pdgamerules.mixin.GameRulesAccess;
import snownee.pdgamerules.mixin.GameRulesValueAccess;

public class PDGameRules extends GameRules {
	private final GameRules parent;
	private final String dimension;
	private final Cache<RuleKey<?>, RuleValue<?>> cache = CacheBuilder.newBuilder().build();

	public PDGameRules(GameRules parent, String dimension) {
		this.parent = parent;
		this.dimension = dimension;
		((GameRulesAccess) this).setRules(Maps.newHashMap());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends RuleValue<T>> T getRule(RuleKey<T> key) {
		try {
			return (T) cache.get(key, () -> {
				Map<String, Object> rules = PDGameRulesConfig.rules.getOrDefault(dimension, ImmutableMap.of());
				Object value = rules.get(key.getId());
				if (value == null) {
					return parent.getRule(key);
				}
				GameRulesValueAccess<T> rule = (GameRulesValueAccess<T>) parent.getRule(key);
				rule = (GameRulesValueAccess<T>) rule.getType().createRule();
				rule.callDeserialize(String.valueOf(value));
				return (T) rule;
			});
		} catch (Exception ignored) {
		}
		return parent.getRule(key);
	}

	public void invalidate() {
		cache.invalidateAll();
	}

	@Override
	public void assignFrom(GameRules rules, @Nullable MinecraftServer server) {
		parent.assignFrom(rules, server);
	}

	@Override
	public GameRules copy() {
		return parent.copy();
	}

	@Override
	public CompoundNBT createTag() {
		return parent.createTag();
	}
}

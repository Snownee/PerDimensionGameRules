package snownee.perworldgamerules;

import java.util.Map;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.minecraft.world.level.GameRules;

public class PerWorldGameRules extends GameRules {
	private final GameRules parent;
	private final String dimension;
	private final Cache<Key<?>, Value<?>> cache = CacheBuilder.newBuilder().build();

	public PerWorldGameRules(GameRules parent, String dimension) {
		super(Map.of());
		this.parent = parent;
		this.dimension = dimension;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Value<T>> T getRule(Key<T> key) {
		try {
			return (T) cache.get(key, () -> {
				Map<String, Object> rules = PWGameRulesConfig.rules.getOrDefault(dimension, Map.of());
				Object value = rules.get(key.getId());
				if (value == null) {
					return parent.getRule(key);
				}
				T rule = parent.getRule(key).type.createRule();
				rule.deserialize(String.valueOf(value));
				return rule;
			});
		} catch (Exception e) {
		}
		return parent.getRule(key);
	}

	public void invalidate() {
		cache.invalidateAll();
	}
}

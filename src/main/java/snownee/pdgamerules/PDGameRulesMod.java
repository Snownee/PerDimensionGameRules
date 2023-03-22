package snownee.pdgamerules;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.world.level.GameRules;

public class PDGameRulesMod implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("PDGameRules");
	public static final Set<GameRules.Key<?>> UNSUPPORTED_GAME_RULES = Sets.newHashSet();
	public static int generation = 0;

	@Override
	public void onInitialize() {
		UNSUPPORTED_GAME_RULES.add(GameRules.RULE_SENDCOMMANDFEEDBACK);
		UNSUPPORTED_GAME_RULES.add(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> PDGameRuleCommand.register(dispatcher));
	}

	public static boolean isSupported(GameRules.Key<?> key) {
		return !UNSUPPORTED_GAME_RULES.contains(key);
	}

}

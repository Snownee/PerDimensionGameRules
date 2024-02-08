package snownee.pdgamerules;

import java.util.Set;

import org.slf4j.Logger;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;

import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("pdgamerules")
public class PDGameRulesMod {

	public static final Logger LOGGER = LogUtils.getLogger();
	public static final Set<GameRules.Key<?>> UNSUPPORTED_GAME_RULES = Sets.newHashSet();
	public static int generation = 0;

	public PDGameRulesMod() {
		UNSUPPORTED_GAME_RULES.add(GameRules.RULE_SENDCOMMANDFEEDBACK);
		UNSUPPORTED_GAME_RULES.add(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
	}

	private void registerCommands(RegisterCommandsEvent event) {
		PDGameRuleCommand.register(event.getDispatcher());
	}

	public static boolean isSupported(GameRules.Key<?> key) {
		return !UNSUPPORTED_GAME_RULES.contains(key);
	}

}

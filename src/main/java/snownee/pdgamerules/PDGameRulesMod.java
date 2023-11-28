package snownee.pdgamerules;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(PDGameRulesMod.ID)
public class PDGameRulesMod {
	public static final String ID = "pdgamerules";
	private static PDGameRulesConfig cfg;
	public static final Logger LOGGER = LogManager.getLogger("PDGameRules");
	public static final Set<GameRules.RuleKey<?>> UNSUPPORTED_GAME_RULES = Sets.newHashSet();
	public static int generation = 0;

	public PDGameRulesMod() {
		UNSUPPORTED_GAME_RULES.add(GameRules.RULE_SENDCOMMANDFEEDBACK);
		UNSUPPORTED_GAME_RULES.add(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
		AutoConfig.register(PDGameRulesConfig.class, JanksonConfigSerializer::new);
		cfg = AutoConfig.getConfigHolder(PDGameRulesConfig.class).getConfig();
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
	}

	private void registerCommands(RegisterCommandsEvent event) {
		PDGameRuleCommand.register(event.getDispatcher());
	}

	public static PDGameRulesConfig getCfg() {
		return cfg;
	}

	public static boolean isSupported(GameRules.RuleKey<?> key) {
		return !UNSUPPORTED_GAME_RULES.contains(key);
	}

}

package snownee.pdgamerules;

import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import snownee.kiwi.config.KiwiConfigManager;
import snownee.pdgamerules.mixin.GameRulesValueAccess;

public class PDGameRuleCommand {
	public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
		final LiteralArgumentBuilder<CommandSource> literalArgumentBuilder = Commands.literal("pdgamerule").requires(commandSourceStack -> commandSourceStack.hasPermission(2));
		GameRules.visitGameRuleTypes(new GameRules.IRuleEntryVisitor() {

			@Override
			public <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> key, GameRules.RuleType<T> type) {
				if (PDGameRulesMod.isSupported(key)) {
					literalArgumentBuilder.then((Commands.literal(key.getId()).executes(commandContext -> PDGameRuleCommand.queryRule(commandContext.getSource(), key))).then(type.createArgument("value").executes(commandContext -> PDGameRuleCommand.setRule(commandContext, key))));
				}
			}

		});
		commandDispatcher.register(literalArgumentBuilder);
	}

	static <T extends GameRules.RuleValue<T>> int setRule(CommandContext<CommandSource> commandContext, GameRules.RuleKey<T> key) {
		CommandSource commandSourceStack = commandContext.getSource();
		T value = ((GameRulesValueAccess<T>) commandSourceStack.getLevel().getGameRules().getRule(key)).getType().createRule();
		value.setFromArgument(commandContext, "value");
		String dimension = commandSourceStack.getLevel().dimension().location().toString();
		Map<String, Object> map = PDGameRulesMod.getCfg().rules.computeIfAbsent(dimension, k -> Maps.newHashMap());
		map.put(key.getId(), value.serialize());
		KiwiConfigManager.getHandler(PDGameRulesConfig.class).getConfig().save();
		PDGameRulesMod.generation++;
		commandSourceStack.sendSuccess(new TranslationTextComponent("commands.gamerule.set", key.getId(), value.toString()), true);
		return value.getCommandResult();
	}

	static <T extends GameRules.RuleValue<T>> int queryRule(CommandSource commandSourceStack, GameRules.RuleKey<T> key) {
		T value = commandSourceStack.getLevel().getGameRules().getRule(key);
		commandSourceStack.sendSuccess(new TranslationTextComponent("commands.gamerule.query", key.getId(), value.toString()), false);
		return value.getCommandResult();
	}

}

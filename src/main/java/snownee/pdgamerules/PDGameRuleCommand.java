package snownee.pdgamerules;

import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;
import snownee.kiwi.config.KiwiConfigManager;
import snownee.pdgamerules.mixin.GameRulesValueAccess;

public class PDGameRuleCommand {
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
		final LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal("pdgamerule").requires(commandSourceStack -> commandSourceStack.hasPermission(2));
		GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {

			@Override
			public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
				if (PDGameRulesMod.isSupported(key)) {
					literalArgumentBuilder.then((Commands.literal(key.getId()).executes(commandContext -> PDGameRuleCommand.queryRule(commandContext.getSource(), key))).then(type.createArgument("value").executes(commandContext -> PDGameRuleCommand.setRule(commandContext, key))));
				}
			}

		});
		commandDispatcher.register(literalArgumentBuilder);
	}

	static <T extends GameRules.Value<T>> int setRule(CommandContext<CommandSourceStack> commandContext, GameRules.Key<T> key) {
		CommandSourceStack commandSourceStack = commandContext.getSource();
		T value = ((GameRulesValueAccess<T>) commandSourceStack.getLevel().getGameRules().getRule(key)).getType().createRule();
		value.setFromArgument(commandContext, "value");
		String dimension = commandSourceStack.getLevel().dimension().location().toString();
		Map<String, Object> map = PDGameRulesConfig.rules.computeIfAbsent(dimension, k -> Maps.newHashMap());
		map.put(key.getId(), value.serialize());
		KiwiConfigManager.getHandler(PDGameRulesConfig.class).save();
		PDGameRulesMod.generation++;
		commandSourceStack.sendSuccess(Component.translatable("commands.gamerule.set", key.getId(), value.toString()), true);
		return value.getCommandResult();
	}

	static <T extends GameRules.Value<T>> int queryRule(CommandSourceStack commandSourceStack, GameRules.Key<T> key) {
		T value = commandSourceStack.getLevel().getGameRules().getRule(key);
		commandSourceStack.sendSuccess(Component.translatable("commands.gamerule.query", key.getId(), value.toString()), false);
		return value.getCommandResult();
	}

}

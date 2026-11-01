package snownee.pdgamerules;

import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.minecraft.world.level.gamerules.GameRules;
import snownee.kiwi.config.KiwiConfigManager;

public class PDGameRuleCommand {
	private static final SimpleCommandExceptionType ERROR_OVERWORLD = new SimpleCommandExceptionType(Component.translatable(
			"commands.pdgamerules.overworldCheck.failed"));

	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher, CommandBuildContext context) {
		final LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal("pdgamerule")
				.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));
		new GameRules(context.enabledFeatures()).visitGameRuleTypes(new GameRuleTypeVisitor() {

			@Override
			public <T> void visit(GameRule<T> rule) {
				if (!PDGameRulesMod.isSupported(rule)) {
					return;
				}
				literalArgumentBuilder.then(Commands.literal(rule.id())
						.executes(commandContext -> PDGameRuleCommand.queryRule(commandContext.getSource(), rule))
						.then(Commands.argument("value", rule.argument())
								.executes(commandContext -> PDGameRuleCommand.setRule(commandContext, rule)))
						.then(Commands.literal("clear!").executes(commandContext -> PDGameRuleCommand.clearRule(commandContext, rule))));
			}

		});
		commandDispatcher.register(literalArgumentBuilder);
	}

	static <T> int setRule(
			CommandContext<CommandSourceStack> commandContext, GameRule<T> rule) throws CommandSyntaxException {
		CommandSourceStack commandSourceStack = commandContext.getSource();
		checkOverworld(commandSourceStack, rule);
		T value = commandContext.getArgument("value", rule.valueClass());
		String dimension = commandSourceStack.getLevel().dimension().identifier().toString();
		Map<String, Object> map = PDGameRulesConfig.rules.computeIfAbsent(dimension, k -> Maps.newHashMap());
		String serialized = rule.serialize(value);
		map.put(rule.id(), serialized);
		KiwiConfigManager.getHandler(PDGameRulesConfig.class).save();
		PDGameRulesMod.onPDRuleChange(commandSourceStack.getLevel(), rule, serialized);
		commandSourceStack.sendSuccess(() -> Component.translatable("commands.gamerule.set", rule.id(), serialized), true);
		return rule.getCommandResult(value);
	}

	static <T> int clearRule(
			CommandContext<CommandSourceStack> commandContext, GameRule<T> rule) throws CommandSyntaxException {
		CommandSourceStack commandSourceStack = commandContext.getSource();
		checkOverworld(commandSourceStack, rule);
		String dimension = commandSourceStack.getLevel().dimension().identifier().toString();
		Map<String, Object> map = PDGameRulesConfig.rules.get(dimension);
		if (map == null || !map.containsKey(rule.id())) {
			commandSourceStack.sendFailure(Component.translatable("commands.pdgamerules.clear.failed", rule.id()));
			return 0;
		}
		map.remove(rule.id());
		KiwiConfigManager.getHandler(PDGameRulesConfig.class).save();
		PDGameRulesMod.onPDRuleChange(commandSourceStack.getLevel(), rule, null);
		commandSourceStack.sendSuccess(() -> Component.translatable("commands.pdgamerules.clear", rule.id()), true);
		return 1;
	}

	static <T> int queryRule(
			CommandSourceStack commandSourceStack, GameRule<T> rule) throws CommandSyntaxException {
		checkOverworld(commandSourceStack, rule);
		T value = commandSourceStack.getLevel().getGameRules().get(rule);
		commandSourceStack.sendSuccess(() -> Component.translatable("commands.gamerule.query", rule.id(), rule.serialize(value)), false);
		return rule.getCommandResult(value);
	}

	static void checkOverworld(CommandSourceStack source, GameRule<?> rule) throws CommandSyntaxException {
		if (Level.OVERWORLD.equals(source.getLevel().dimension()) && !PDGameRulesMod.canUseInOverworld(rule)) {
			throw ERROR_OVERWORLD.create();
		}
	}

}

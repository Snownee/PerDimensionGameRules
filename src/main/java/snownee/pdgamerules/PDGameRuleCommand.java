package snownee.pdgamerules;

import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import snownee.kiwi.config.KiwiConfigManager;
import snownee.pdgamerules.mixin.GameRulesValueAccess;

public class PDGameRuleCommand {
	private static final SimpleCommandExceptionType ERROR_OVERWORLD = new SimpleCommandExceptionType(
			Component.translatable("commands.pdgamerules.overworldCheck.failed"));

	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
		final LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal("pdgamerule")
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2));
		GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {

			@Override
			public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
				if (!PDGameRulesMod.isSupported(key)) {
					return;
				}
				literalArgumentBuilder.then((Commands.literal(key.getId())
						.executes(commandContext -> PDGameRuleCommand.queryRule(commandContext.getSource(), key)))
						.then(type.createArgument("value")
								.executes(commandContext -> PDGameRuleCommand.setRule(commandContext, key)))
						.then(Commands.literal("clear!")
								.executes(commandContext -> PDGameRuleCommand.clearRule(commandContext, key))));
			}

		});
		commandDispatcher.register(literalArgumentBuilder);
	}

	static <T extends GameRules.Value<T>> int setRule(CommandContext<CommandSourceStack> commandContext, GameRules.Key<T> key) throws CommandSyntaxException {
		CommandSourceStack commandSourceStack = commandContext.getSource();
		checkOverworld(commandSourceStack, key);
		T value = ((GameRulesValueAccess<T>) commandSourceStack.getLevel().getGameRules().getRule(key)).getType().createRule();
		// though the PDGameRules object will be replaced immediately, we still need to check if the value is valid
		value.setFromArgument(commandContext, "value");
		String dimension = commandSourceStack.getLevel().dimension().location().toString();
		Map<String, Object> map = PDGameRulesConfig.rules.computeIfAbsent(dimension, k -> Maps.newHashMap());
		String serialized = value.serialize();
		map.put(key.getId(), serialized);
		KiwiConfigManager.getHandler(PDGameRulesConfig.class).save();
		PDGameRulesMod.onPDRuleChange(commandSourceStack.getLevel(), key, serialized);
		commandSourceStack.sendSuccess(() -> Component.translatable("commands.gamerule.set", key.getId(), value.toString()), true);
		return value.getCommandResult();
	}

	static <T extends GameRules.Value<T>> int clearRule(CommandContext<CommandSourceStack> commandContext, GameRules.Key<T> key) throws CommandSyntaxException {
		CommandSourceStack commandSourceStack = commandContext.getSource();
		checkOverworld(commandSourceStack, key);
		String dimension = commandSourceStack.getLevel().dimension().location().toString();
		Map<String, Object> map = PDGameRulesConfig.rules.get(dimension);
		if (map == null || !map.containsKey(key.getId())) {
			commandSourceStack.sendFailure(Component.translatable("commands.pdgamerules.clear.failed", key.getId()));
			return 0;
		}
		map.remove(key.getId());
		KiwiConfigManager.getHandler(PDGameRulesConfig.class).save();
		PDGameRulesMod.onPDRuleChange(commandSourceStack.getLevel(), key, null);
		commandSourceStack.sendSuccess(() -> Component.translatable("commands.pdgamerules.clear", key.getId()), true);
		return 1;
	}

	static <T extends GameRules.Value<T>> int queryRule(CommandSourceStack commandSourceStack, GameRules.Key<T> key) throws CommandSyntaxException {
		checkOverworld(commandSourceStack, key);
		T value = commandSourceStack.getLevel().getGameRules().getRule(key);
		commandSourceStack.sendSuccess(() -> Component.translatable("commands.gamerule.query", key.getId(), value.toString()), false);
		return value.getCommandResult();
	}

	static void checkOverworld(CommandSourceStack source, GameRules.Key<?> key) throws CommandSyntaxException {
		if (source.getLevel().dimension() == Level.OVERWORLD && !PDGameRulesMod.canUseInOverworld(key)) {
			throw ERROR_OVERWORLD.create();
		}
	}

}

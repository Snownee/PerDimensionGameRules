package snownee.pdgamerules.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.GameRules;

@Mixin(GameRules.RuleValue.class)
public interface GameRulesValueAccess<T extends GameRules.RuleValue<T>> {

	@Accessor
	GameRules.RuleType<T> getType();

	@Invoker
	void callDeserialize(String value);

}

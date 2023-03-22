package snownee.pdgamerules.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.level.GameRules;

@Mixin(GameRules.Value.class)
public interface GameRulesValueAccess<T extends GameRules.Value<T>> {

	@Accessor
	GameRules.Type<T> getType();

	@Invoker
	void callDeserialize(String value);

}

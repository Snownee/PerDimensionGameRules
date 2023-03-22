package snownee.pdgamerules.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.GameRules;

@Mixin(GameRules.class)
public interface GameRulesAccess {

	@Accessor
	@Mutable
	void setRules(Map<GameRules.Key<?>, GameRules.Value<?>> rules);

}

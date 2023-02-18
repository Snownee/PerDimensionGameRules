package snownee.perworldgamerules.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import snownee.perworldgamerules.PWGameRulesMod;
import snownee.perworldgamerules.PerWorldGameRules;

@Mixin(Level.class)
public class LevelMixin {

	private PerWorldGameRules pwgamerules_gameRules;
	private int pwgamerules_generation;

	@Inject(method = "getGameRules", at = @At("HEAD"), cancellable = true)
	private void pwgamerules_getGameRules(CallbackInfoReturnable<GameRules> ci) {
		if (pwgamerules_generation != PWGameRulesMod.generation) {
			pwgamerules_generation = PWGameRulesMod.generation;
			pwgamerules_gameRules = null;
		}
		if (pwgamerules_gameRules == null) {
			Level level = (Level) (Object) this;
			pwgamerules_gameRules = new PerWorldGameRules(level.getLevelData().getGameRules(), level.dimension().location().toString());
		}
		ci.setReturnValue(pwgamerules_gameRules);
	}

}

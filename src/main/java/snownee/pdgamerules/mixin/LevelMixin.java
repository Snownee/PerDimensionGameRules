package snownee.pdgamerules.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import snownee.pdgamerules.PDGameRulesMod;
import snownee.pdgamerules.PDGameRules;

@Mixin(Level.class)
public class LevelMixin {

	private PDGameRules pwgamerules_gameRules;
	private int pwgamerules_generation;

	@Inject(method = "getGameRules", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_getGameRules(CallbackInfoReturnable<GameRules> ci) {
		if (pwgamerules_generation != PDGameRulesMod.generation) {
			pwgamerules_generation = PDGameRulesMod.generation;
			pwgamerules_gameRules = null;
		}
		if (pwgamerules_gameRules == null) {
			Level level = (Level) (Object) this;
			pwgamerules_gameRules = new PDGameRules(level.getLevelData().getGameRules(), level.dimension().location().toString());
		}
		ci.setReturnValue(pwgamerules_gameRules);
	}

}

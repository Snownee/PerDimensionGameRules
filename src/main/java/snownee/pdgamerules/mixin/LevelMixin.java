package snownee.pdgamerules.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import snownee.pdgamerules.PDGameRules;
import snownee.pdgamerules.PDGameRulesMod;

@Mixin(World.class)
public class LevelMixin {

	@Unique
	private PDGameRules pdgamerules_gameRules;
	@Unique
	private int pdgamerules_generation;

	@Inject(method = "getGameRules", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_getGameRules(CallbackInfoReturnable<GameRules> ci) {
		if (pdgamerules_generation != PDGameRulesMod.generation) {
			pdgamerules_generation = PDGameRulesMod.generation;
			pdgamerules_gameRules = null;
		}
		if (pdgamerules_gameRules == null) {
			World level = (World) (Object) this;
			pdgamerules_gameRules = new PDGameRules(level.getLevelData().getGameRules(), level.dimension().location().toString());
		}
		ci.setReturnValue(pdgamerules_gameRules);
	}

}

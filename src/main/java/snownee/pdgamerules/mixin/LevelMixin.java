package snownee.pdgamerules.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import snownee.pdgamerules.PDGameRules;
import snownee.pdgamerules.PDGameRulesMod;

@Mixin(Level.class)
public class LevelMixin {

	@Shadow
	@Final
	public boolean isClientSide;
	@Unique
	private PDGameRules pdgamerules$gameRules;
	@Unique
	private int pdgamerules$generation;

	@Inject(method = "getGameRules", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_getGameRules(CallbackInfoReturnable<GameRules> ci) {
		if (isClientSide) {
			return;
		}
		if (pdgamerules$generation != PDGameRulesMod.getGeneration()) {
			pdgamerules$generation = PDGameRulesMod.getGeneration();
			pdgamerules$gameRules = null;
			PDGameRulesMod.LOGGER.debug("Invalidating game rules cache");
		}
		if (pdgamerules$gameRules == null) {
			Level level = (Level) (Object) this;
			pdgamerules$gameRules = new PDGameRules(level.getLevelData().getGameRules(), level.dimension());
		}
		ci.setReturnValue(pdgamerules$gameRules);
	}

}

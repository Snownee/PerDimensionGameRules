package snownee.pdgamerules.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import snownee.pdgamerules.PDGameRulesMod;

@Mixin(GameRules.RuleValue.class)
public class GameRulesValueMixin {

	@Inject(method = "onChanged", at = @At("HEAD"))
	private void pdgamerules_onChanged(MinecraftServer minecraftServer, CallbackInfo ci) {
		PDGameRulesMod.generation++;
	}

}

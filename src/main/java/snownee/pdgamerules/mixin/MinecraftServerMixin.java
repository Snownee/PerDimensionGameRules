package snownee.pdgamerules.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRule;
import snownee.pdgamerules.PDGameRulesMod;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Inject(method = "onGameRuleChanged", at = @At("HEAD"))
	private <T> void pdgamerules_onGameRuleChanged(GameRule<T> rule, T value, CallbackInfo ci) {
		PDGameRulesMod.onGlobalRuleChange(rule);
	}

}

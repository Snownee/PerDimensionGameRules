package snownee.pdgamerules.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import snownee.pdgamerules.PDGameRulesMod;

@Mixin(GameRules.Value.class)
public class GameRulesValueMixin {

	@Inject(method = "onChanged", at = @At("HEAD"))
	private void pdgamerules_onChanged(@Nullable MinecraftServer server, CallbackInfo ci) {
		if (server != null) {
			PDGameRulesMod.iterateGeneration();
		}
	}

}

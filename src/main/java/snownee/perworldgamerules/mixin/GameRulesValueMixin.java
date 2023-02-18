package snownee.perworldgamerules.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import snownee.perworldgamerules.PWGameRulesMod;

@Mixin(GameRules.Value.class)
public class GameRulesValueMixin {

	@Inject(method = "onChanged", at = @At("HEAD"))
	private void pwgamerules_onChanged(MinecraftServer minecraftServer, CallbackInfo ci) {
		PWGameRulesMod.generation++;
	}

}

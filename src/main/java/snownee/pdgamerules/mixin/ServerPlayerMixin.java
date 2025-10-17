package snownee.pdgamerules.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
	@WrapOperation(
			method = "restoreFrom",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getGameRules()Lnet/minecraft/world/level/GameRules;"))
	private GameRules pdgamerules_getGameRules(Level level, Operation<GameRules> original, @Local(argsOnly = true) ServerPlayer player) {
		return player.level().getGameRules();
	}
}

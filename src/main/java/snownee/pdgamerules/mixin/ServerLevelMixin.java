package snownee.pdgamerules.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.WritableLevelData;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

	//TODO: use MixinExtras after moving to NeoForge
	@Redirect(method = "tickTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/WritableLevelData;getGameRules()Lnet/minecraft/world/level/GameRules;"))
	private GameRules pdgamerules_getGameRules(WritableLevelData levelData) {
		return ((ServerLevel) (Object) this).getGameRules();
	}

}

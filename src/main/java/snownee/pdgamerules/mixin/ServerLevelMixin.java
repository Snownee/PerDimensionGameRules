package snownee.pdgamerules.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import snownee.pdgamerules.duck.PDDerivedLevelData;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

	@Mutable
	@Shadow
	@Final
	private boolean tickTime;

	@Shadow
	protected abstract void tickTime();

	@Shadow
	@Final
	private ServerLevelData serverLevelData;

	//TODO: use MixinExtras after moving to NeoForge
	@Redirect(method = "tickTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/WritableLevelData;getGameRules()Lnet/minecraft/world/level/GameRules;"))
	private GameRules pdgamerules_getGameRules(WritableLevelData levelData) {
		return ((ServerLevel) (Object) this).getGameRules();
	}

	//TODO: use MixinExtras after moving to NeoForge
	@Inject(method = "tickTime", at = @At("HEAD"))
	private void pdgamerules_forceTickTime(CallbackInfo ci) {
		if (!tickTime && serverLevelData instanceof PDDerivedLevelData data && data.pdgamerules$isIndependentDayTime()) {
			tickTime = true;
			tickTime();
			tickTime = false;
		}
	}

}

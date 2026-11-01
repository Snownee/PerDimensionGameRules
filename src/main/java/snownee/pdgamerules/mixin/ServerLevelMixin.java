package snownee.pdgamerules.mixin;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.saveddata.WeatherData;
import snownee.pdgamerules.PDGameRules;
import snownee.pdgamerules.PDGameRulesMod;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
	@Unique
	private @Nullable PDGameRules pdgamerules$gameRules;
	@Unique
	private int pdgamerules$generation = -1;

	@Inject(method = "getGameRules", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_getGameRules(CallbackInfoReturnable<GameRules> ci) {
		if (pdgamerules$generation != PDGameRulesMod.getGeneration()) {
			pdgamerules$generation = PDGameRulesMod.getGeneration();
			pdgamerules$gameRules = null;
			PDGameRulesMod.LOGGER.debug("Invalidating game rules cache");
		}
		if (pdgamerules$gameRules == null) {
			ServerLevel level = (ServerLevel) (Object) this;
			pdgamerules$gameRules = new PDGameRules(
					level.getServer().getGameRules(),
					level.dimension(),
					level.dimensionTypeRegistration().unwrapKey().map($ -> $.identifier().toString()).orElse(null));
		}
		ci.setReturnValue(pdgamerules$gameRules);
	}

	@Inject(method = "getWeatherData", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_getWeatherData(CallbackInfoReturnable<WeatherData> ci) {
		ServerLevel level = (ServerLevel) (Object) this;
		if (PDGameRulesMod.hasIndependentWeather(level)) {
			ci.setReturnValue(PDGameRulesMod.getIndependentWeather(level));
		}
	}

	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/MinecraftServer;getWeatherData()Lnet/minecraft/world/level/saveddata/WeatherData;"))
	private WeatherData pdgamerules_prepareWeather(MinecraftServer server) {
		ServerLevel level = (ServerLevel) (Object) this;
		if (PDGameRulesMod.hasIndependentWeather(level)) {
			return PDGameRulesMod.getIndependentWeather(level);
		}
		return server.getWeatherData();
	}

}

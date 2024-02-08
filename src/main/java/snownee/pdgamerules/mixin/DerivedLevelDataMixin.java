package snownee.pdgamerules.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import snownee.pdgamerules.PDDerivedGameRuleData;
import snownee.pdgamerules.duck.PDDerivedLevelData;

@Mixin(DerivedLevelData.class)
public abstract class DerivedLevelDataMixin implements PDDerivedLevelData {
	@Shadow
	@Final
	private ServerLevelData wrapped;

	@Unique
	private PDDerivedGameRuleData pdgamerules$data = PDDerivedGameRuleData.DUMMY;

	@Inject(method = "getDayTime", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_getDayTime(CallbackInfoReturnable<Long> ci) {
		if (pdgamerules$data.independentDayTime) {
			ci.setReturnValue(pdgamerules$data.dayTime);
		}
	}

	@Inject(method = "getClearWeatherTime", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_getClearWeatherTime(CallbackInfoReturnable<Integer> ci) {
		if (pdgamerules$data.independentWeather) {
			ci.setReturnValue(pdgamerules$data.clearWeatherTime);
		}
	}

	@Inject(method = "setClearWeatherTime", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_setClearWeatherTime(int clearWeatherTime, CallbackInfo ci) {
		if (pdgamerules$data.independentWeather) {
			pdgamerules$data.clearWeatherTime = clearWeatherTime;
			ci.cancel();
		}
	}

	@Inject(method = "isThundering", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_isThundering(CallbackInfoReturnable<Boolean> ci) {
		if (pdgamerules$data.independentWeather) {
			ci.setReturnValue(pdgamerules$data.thundering);
		}
	}

	@Inject(method = "getThunderTime", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_getThunderTime(CallbackInfoReturnable<Integer> ci) {
		if (pdgamerules$data.independentWeather) {
			ci.setReturnValue(pdgamerules$data.thunderTime);
		}
	}

	@Inject(method = "isRaining", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_isRaining(CallbackInfoReturnable<Boolean> ci) {
		if (pdgamerules$data.independentWeather) {
			ci.setReturnValue(pdgamerules$data.raining);
		}
	}

	@Inject(method = "getRainTime", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_getRainTime(CallbackInfoReturnable<Integer> ci) {
		if (pdgamerules$data.independentWeather) {
			ci.setReturnValue(pdgamerules$data.rainTime);
		}
	}

	@Inject(method = "setDayTime", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_setDayTime(long dayTime, CallbackInfo ci) {
		if (pdgamerules$data.independentDayTime) {
			pdgamerules$data.dayTime = dayTime;
			ci.cancel();
		}
	}

	@Inject(method = "setThundering", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_setThundering(boolean thundering, CallbackInfo ci) {
		if (pdgamerules$data.independentWeather) {
			pdgamerules$data.thundering = thundering;
			ci.cancel();
		}
	}

	@Inject(method = "setThunderTime", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_setThunderTime(int thunderTime, CallbackInfo ci) {
		if (pdgamerules$data.independentWeather) {
			pdgamerules$data.thunderTime = thunderTime;
			ci.cancel();
		}
	}

	@Inject(method = "setRaining", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_setRaining(boolean raining, CallbackInfo ci) {
		if (pdgamerules$data.independentWeather) {
			pdgamerules$data.raining = raining;
			ci.cancel();
		}
	}

	@Inject(method = "setRainTime", at = @At("HEAD"), cancellable = true)
	private void pdgamerules_setRainTime(int rainTime, CallbackInfo ci) {
		if (pdgamerules$data.independentWeather) {
			pdgamerules$data.rainTime = rainTime;
			ci.cancel();
		}
	}

	@Override
	public void pdgamerules$setIndependentDayTime(boolean independentDayTime) {
		pdgamerules$data.independentDayTime = independentDayTime;
		DerivedLevelData self = (DerivedLevelData) (Object) this;
		self.setDayTime(wrapped.getDayTime());
	}

	@Override
	public void pdgamerules$setIndependentWeather(boolean independentWeather) {
		pdgamerules$data.independentWeather = independentWeather;
		DerivedLevelData self = (DerivedLevelData) (Object) this;
		self.setRaining(wrapped.isRaining());
		self.setRainTime(wrapped.getRainTime());
		self.setThundering(wrapped.isThundering());
		self.setThunderTime(wrapped.getThunderTime());
		self.setClearWeatherTime(wrapped.getClearWeatherTime());
	}

	@Override
	public boolean pdgamerules$isIndependentDayTime() {
		return pdgamerules$data.independentDayTime;
	}

	@Override
	public boolean pdgamerules$isIndependentWeather() {
		return pdgamerules$data.independentWeather;
	}

	@Override
	public void pdgamerules$putData(PDDerivedGameRuleData data) {
		pdgamerules$data = data;
	}
}

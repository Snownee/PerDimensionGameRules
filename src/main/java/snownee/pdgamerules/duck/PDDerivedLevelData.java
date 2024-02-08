package snownee.pdgamerules.duck;

import snownee.pdgamerules.PDDerivedGameRuleData;

public interface PDDerivedLevelData {
	void pdgamerules$setIndependentDayTime(boolean independentDayTime);

	void pdgamerules$setIndependentWeather(boolean independentWeather);

	boolean pdgamerules$isIndependentDayTime();

	boolean pdgamerules$isIndependentWeather();

	void pdgamerules$putData(PDDerivedGameRuleData data);
}

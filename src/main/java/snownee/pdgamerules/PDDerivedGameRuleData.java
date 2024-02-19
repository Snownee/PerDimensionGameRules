package snownee.pdgamerules;

import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class PDDerivedGameRuleData {
	public static final PDDerivedGameRuleData DUMMY = new PDDerivedGameRuleData();

	public static final Codec<PDDerivedGameRuleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("independentDayTime").forGetter(data -> data.independentDayTime),
			Codec.BOOL.fieldOf("independentWeather").forGetter(data -> data.independentWeather),
			Codec.LONG.fieldOf("dayTime").forGetter(data -> data.dayTime),
			Codec.INT.fieldOf("clearWeatherTime").forGetter(data -> data.clearWeatherTime),
			Codec.BOOL.fieldOf("raining").forGetter(data -> data.raining),
			Codec.INT.fieldOf("rainTime").forGetter(data -> data.rainTime),
			Codec.BOOL.fieldOf("thundering").forGetter(data -> data.thundering),
			Codec.INT.fieldOf("thunderTime").forGetter(data -> data.thunderTime)
	).apply(instance, PDDerivedGameRuleData::new));

	public static final Codec<Map<ResourceKey<Level>, PDDerivedGameRuleData>> DATA_MAP_CODEC = Codec.unboundedMap(
			ResourceKey.codec(Registries.DIMENSION),
			PDDerivedGameRuleData.CODEC);

	public PDDerivedGameRuleData(
			boolean independentDayTime,
			boolean independentWeather,
			long dayTime,
			int clearWeatherTime,
			boolean raining,
			int rainTime,
			boolean thundering,
			int thunderTime) {
		this.independentDayTime = independentDayTime;
		this.independentWeather = independentWeather;
		this.dayTime = dayTime;
		this.clearWeatherTime = clearWeatherTime;
		this.raining = raining;
		this.rainTime = rainTime;
		this.thundering = thundering;
		this.thunderTime = thunderTime;
	}

	public PDDerivedGameRuleData() {
	}

	public boolean independentDayTime;

	public boolean independentWeather;

	public long dayTime;

	public int clearWeatherTime;

	public boolean raining;

	public int rainTime;

	public boolean thundering;

	public int thunderTime;
}

package com.fumbbl.ffb.factory.bb2020;

import com.fumbbl.ffb.KickoffResult;
import com.fumbbl.ffb.RulesCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RulesCollection(RulesCollection.Rules.BB2020)
public class KickoffResultMapping extends com.fumbbl.ffb.KickoffResultMapping {
	private final Map<Integer, KickoffResult> results = new HashMap<Integer, KickoffResult>() {{
		put(2, KickoffResult.GET_THE_REF);
		put(3, KickoffResult.RIOT);
		put(4, KickoffResult.PERFECT_DEFENCE);
		put(5, KickoffResult.HIGH_KICK);
		put(6, KickoffResult.CHEERING_FANS);
		put(7, KickoffResult.WEATHER_CHANGE);
		put(8, KickoffResult.BRILLIANT_COACHING);
		put(9, KickoffResult.QUICK_SNAP);
		put(10, KickoffResult.BLITZ);
		put(11, KickoffResult.THROW_A_ROCK);
		put(12, KickoffResult.PITCH_INVASION);
	}};

	@Override
	public KickoffResult getResult(int roll) {
		return results.get(roll);
	}

	@Override
	public Collection<KickoffResult> getValues() {
		return results.values();
	}
}

package com.fumbbl.ffb.report.mixed;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.BreatheFireResult;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ReportBreatheFire extends ReportSkillRoll {
	private String defenderId;
	private boolean strongOpponent;
	private BreatheFireResult result;

	public ReportBreatheFire() {
	}

	public ReportBreatheFire(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
                           boolean pReRolled, String defenderId, BreatheFireResult result, boolean strongOpponent) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, null);
		this.defenderId = defenderId;
		this.strongOpponent = strongOpponent;
		this.result = result;
	}

	@Override
	public ReportId getId() {
		return ReportId.BREATHE_FIRE;
	}

	@Override
	public ReportBreatheFire transform(IFactorySource source) {
		return new ReportBreatheFire(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			defenderId, result, strongOpponent);
	}

	public String getDefenderId() {
		return defenderId;
	}

	public boolean isStrongOpponent() {
		return strongOpponent;
	}

	public BreatheFireResult getResult() {
		return result;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.DEFENDER_ID.addTo(jsonObject, defenderId);
		IJsonOption.STRONG_OPPONENT.addTo(jsonObject, strongOpponent);
		IJsonOption.STATUS.addTo(jsonObject, result.name());
		return jsonObject;
	}

	@Override
	public ReportBreatheFire initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		defenderId = IJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		strongOpponent = IJsonOption.STRONG_OPPONENT.getFrom(source, jsonObject);
		result = BreatheFireResult.valueOf(IJsonOption.STATUS.getFrom(source, jsonObject));
		return this;
	}
}

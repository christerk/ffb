package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportHypnoticGazeRoll extends ReportSkillRoll {

	private String defenderId;

	public ReportHypnoticGazeRoll() {
	}

	public ReportHypnoticGazeRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll,
	                              boolean pReRolled, RollModifier<?>[] pRollModifiers, String defenderId) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
		this.defenderId = defenderId;
	}

	public String getDefenderId() {
		return defenderId;
	}

	@Override
	public ReportId getId() {
		return ReportId.HYPNOTIC_GAZE_ROLL;
	}

	@Override
	public ReportHypnoticGazeRoll transform(IFactorySource source) {
		return new ReportHypnoticGazeRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
			getRollModifiers(), defenderId);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.DEFENDER_ID.addTo(jsonObject, defenderId);
		return jsonObject;
	}

	@Override
	public ReportSkillRoll initFrom(IFactorySource source, JsonValue pJsonValue) {
		super.initFrom(source, pJsonValue);
		defenderId = IJsonOption.DEFENDER_ID.getFrom(source, UtilJson.toJsonObject(pJsonValue));
		return this;
	}
}

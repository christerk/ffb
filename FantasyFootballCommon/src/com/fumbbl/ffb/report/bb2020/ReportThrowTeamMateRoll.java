package com.fumbbl.ffb.report.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.BB2020)
public class ReportThrowTeamMateRoll extends ReportSkillRoll {

	private String fThrownPlayerId;
	private PassingDistance fPassingDistance;
	private PassResult passResult;

	public ReportThrowTeamMateRoll() {
	}

	public ReportThrowTeamMateRoll(String pThrowerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled,
	                               PassModifier[] pPassModifiers, PassingDistance pPassingDistance, String pThrownPlayerId,
	                               PassResult passResult) {
		super(pThrowerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pPassModifiers);
		fThrownPlayerId = pThrownPlayerId;
		fPassingDistance = pPassingDistance;
		this.passResult = passResult;
	}

	public String getThrownPlayerId() {
		return fThrownPlayerId;
	}

	public PassingDistance getPassingDistance() {
		return fPassingDistance;
	}

	public PassResult getPassResult() {
		return passResult;
	}

	@Override
	public ReportId getId() {
		return ReportId.THROW_TEAM_MATE_ROLL;
	}

	@SuppressWarnings("SuspiciousToArrayCall")
	@Override
	public PassModifier[] getRollModifiers() {
		return getRollModifierList().toArray(new PassModifier[0]);
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportThrowTeamMateRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
				getRollModifiers(), getPassingDistance(), getThrownPlayerId(), passResult);
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
		IJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
		IJsonOption.PASSING_DISTANCE.addTo(jsonObject, fPassingDistance);
		IJsonOption.PASS_RESULT.addTo(jsonObject, passResult);
		return jsonObject;
	}

	@Override
	public ReportThrowTeamMateRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fThrownPlayerId = IJsonOption.THROWN_PLAYER_ID.getFrom(game, jsonObject);
		fPassingDistance = (PassingDistance) IJsonOption.PASSING_DISTANCE.getFrom(game, jsonObject);
		passResult = (PassResult) IJsonOption.PASS_RESULT.getFrom(game, jsonObject);
		return this;
	}

}

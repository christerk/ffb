package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportDauntlessRoll extends ReportSkillRoll {

	private int fStrength;
	private String defenderId;

	public ReportDauntlessRoll() {
	}

	public ReportDauntlessRoll(String playerId, boolean successful, int roll, int minimumRoll, boolean reRolled, int strength, String defenderId) {
		this(playerId, successful, roll, minimumRoll, reRolled, strength);
		this.defenderId = defenderId;
	}

	public ReportDauntlessRoll(String playerId, boolean successful, int roll, int minimumRoll, boolean reRolled,
	                           int strength) {
		super(playerId, successful, roll, minimumRoll, reRolled, null);
		fStrength = strength;
	}

	public ReportId getId() {
		return ReportId.DAUNTLESS_ROLL;
	}

	public int getStrength() {
		return fStrength;
	}

	public String getDefenderId() {
		return defenderId;
	}
// transformation

	public IReport transform(IFactorySource source) {
		return new ReportDauntlessRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
				getStrength(), getDefenderId());
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
		IJsonOption.STRENGTH.addTo(jsonObject, fStrength);
		IJsonOption.DEFENDER_ID.addTo(jsonObject, defenderId);
		return jsonObject;
	}

	@Override
	public ReportDauntlessRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fStrength = IJsonOption.STRENGTH.getFrom(game, jsonObject);
		defenderId = IJsonOption.DEFENDER_ID.getFrom(game, jsonObject);
		return this;
	}

}

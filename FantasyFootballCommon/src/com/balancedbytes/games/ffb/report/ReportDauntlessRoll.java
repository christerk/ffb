package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportDauntlessRoll extends ReportSkillRoll {

	private int fStrength;

	public ReportDauntlessRoll() {
	}

	public ReportDauntlessRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled,
			int pStrength) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, null);
		fStrength = pStrength;
	}

	public ReportId getId() {
		return ReportId.DAUNTLESS_ROLL;
	}

	public int getStrength() {
		return fStrength;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportDauntlessRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
				getStrength());
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
		IJsonOption.STRENGTH.addTo(jsonObject, fStrength);
		return jsonObject;
	}

	@Override
	public ReportDauntlessRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fStrength = IJsonOption.STRENGTH.getFrom(game, jsonObject);
		return this;
	}

}

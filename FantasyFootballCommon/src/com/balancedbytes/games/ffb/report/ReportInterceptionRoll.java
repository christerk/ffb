package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportInterceptionRoll extends ReportSkillRoll {

	private boolean fBomb;

	public ReportInterceptionRoll() {
	}

	public ReportInterceptionRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled,
			InterceptionModifier[] pModifiers, boolean pBomb) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pModifiers);
		fBomb = pBomb;
	}

	public ReportId getId() {
		return ReportId.INTERCEPTION_ROLL;
	}

	@Override
	public InterceptionModifier[] getRollModifiers() {
		return getRollModifierList().toArray(new InterceptionModifier[0]);
	}

	public boolean isBomb() {
		return fBomb;
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportInterceptionRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
				getRollModifiers(), isBomb());
	}

	// JSON serialization

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = UtilJson.toJsonObject(super.toJsonValue());
		IJsonOption.BOMB.addTo(jsonObject, fBomb);
		return jsonObject;
	}

	@Override
	public ReportInterceptionRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fBomb = IJsonOption.BOMB.getFrom(game, jsonObject);
		return this;
	}

}

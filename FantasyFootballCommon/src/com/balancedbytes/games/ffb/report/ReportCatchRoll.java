package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.modifiers.CatchModifier;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ReportCatchRoll extends ReportSkillRoll {

	private boolean fBomb;

	public ReportCatchRoll() {
	}

	public ReportCatchRoll(String pPlayerId, boolean pSuccessful, int pRoll, int pMinimumRoll, boolean pReRolled,
			CatchModifier[] pRollModifiers, boolean pBomb) {
		super(pPlayerId, pSuccessful, pRoll, pMinimumRoll, pReRolled, pRollModifiers);
		fBomb = pBomb;
	}

	@Override
	public ReportId getId() {
		return ReportId.CATCH_ROLL;
	}


	public boolean isBomb() {
		return fBomb;
	}

	@Override
	public CatchModifier[] getRollModifiers() {
		return getRollModifierList().toArray(new CatchModifier[getRollModifierList().size()]);
	}

	// transformation

	public IReport transform(IFactorySource source) {
		return new ReportCatchRoll(getPlayerId(), isSuccessful(), getRoll(), getMinimumRoll(), isReRolled(),
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
	public ReportCatchRoll initFrom(IFactorySource game, JsonValue pJsonValue) {
		super.initFrom(game, pJsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		fBomb = IJsonOption.BOMB.getFrom(game, jsonObject);
		return this;
	}

}

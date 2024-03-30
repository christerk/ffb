package com.fumbbl.ffb.report;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.modifiers.CatchModifier;

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
	public ReportCatchRoll initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fBomb = IJsonOption.BOMB.getFrom(source, jsonObject);
		return this;
	}

}
